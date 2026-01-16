package de.thkoeln.chessfed.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.thkoeln.chessfed.dto.JsonResourceDescriptorDto;
import de.thkoeln.chessfed.dto.LinkDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.exception.InvalidAcctException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.IActorRepository;
import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ActorService implements IActorService {

    private IFederationService federationService;
    private ILocalUserRepository userRepository;
    private IActorRepository actorRepository;
    private RestClient client = RestClient.create();

    @Autowired
    public ActorService(IFederationService federationService, ILocalUserRepository userRepository, IActorRepository actorRepository) {
        this.federationService = federationService;
        this.userRepository = userRepository;
        this.actorRepository = actorRepository;
    }

    @Override
    public Actor getActorByAcct(String acct) {
        if (!acct.startsWith("acct:")) throw new InvalidAcctException();
        String[] parts = acct.substring(5).split("@");
        if (parts.length != 2) throw new InvalidAcctException();
        if (parts[1].equals(federationService.getDomain())) {
            return getActorById(parts[0]);
        } else {
            return actorRepository.getByLocalpartAndDomain(parts[0], parts[1]).or(() -> this.resolveRemoteActor(acct, parts[0], parts[1])).orElseThrow(ResourceNotFoundException::new);   
        }
    }

    @Override
    public Actor getActorById(String id) {
        return actorRepository.getByLocalpartAndDomain(id, federationService.getDomain()).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Actor getActorByUrl(String url) {
        if ((federationService.getBaseUrl() + "/instance").equals(url)) return getInstanceActor();
        try {
            return actorRepository.getReferenceById(url);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public Actor getInstanceActor() {
        Actor actor = new Actor();
        actor.setLocalpart(federationService.getDomain());
        actor.setDomain(federationService.getDomain());
        actor.setId(federationService.getBaseUrl() + "/instance");
        actor.setInbox(actor.getId() + "/inbox");
        actor.setOutbox(actor.getId() + "/outbox");
        return actor;
    }

    @Override
    public LocalUser createUser(String username) {
        Actor actor = new Actor();
        actor.setDomain(federationService.getDomain());
        actor.setLocalpart(username);
        actor.setId(federationService.getBaseUrl() + "/users/" + username);
        actor.setInbox(actor.getId() + "/inbox");
        actor.setOutbox(actor.getId() + "/outbox");
        actorRepository.save(actor);
        LocalUser user = new LocalUser();
        user.setUsername(username);
        user.setActor(actor);
        userRepository.save(user);
        return user;
    }

    private Optional<Actor> resolveRemoteActor(String acct, String localpart, String domain) {
        try {
            JsonResourceDescriptorDto res = client.get().uri("https://{domain}/.well-known/webfinger?resource={acct}", domain, acct).retrieve().body(JsonResourceDescriptorDto.class);
            String url = null;
            for (LinkDto link : res.getLinks()) {
                if ("self".equals(link.getRel())) {
                    url = link.getHref();
                    break;
                }
            }
            if (url != null) {
                Map<String, Object> actorJson = client.get().uri(url).accept(MediaType.valueOf("application/activity+json")).retrieve().body(Map.class);
                System.out.println(actorJson.keySet());
                Actor actor = new Actor();
                actor.setLocalpart(localpart);
                actor.setDomain(domain);
                actor.setId(url);
                if (actorJson.containsKey("inbox")) actor.setInbox((String) actorJson.get("inbox"));
                if (actorJson.containsKey("outbox")) actor.setOutbox((String) actorJson.get("outbox"));
                actorRepository.save(actor);
                return Optional.of(actor);
            }
        } catch(Exception e) {
                // :P    
        }
        return Optional.empty();
    }

}
