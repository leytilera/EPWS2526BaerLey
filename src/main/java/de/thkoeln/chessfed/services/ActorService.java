package de.thkoeln.chessfed.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.thkoeln.chessfed.dto.JsonResourceDescriptorDto;
import de.thkoeln.chessfed.dto.LinkDto;
import de.thkoeln.chessfed.exception.ActorNotFoundException;
import de.thkoeln.chessfed.exception.InvalidAcctException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;

@Service
public class ActorService implements IActorService {

    private IFederationService federationService;
    private ILocalUserRepository userRepository;
    private RestClient client = RestClient.create();

    @Autowired
    public ActorService(IFederationService federationService, ILocalUserRepository userRepository) {
        this.federationService = federationService;
        this.userRepository = userRepository;
    }

    @Override
    public Actor getActorByAcct(String acct) {
        if (!acct.startsWith("acct:")) throw new InvalidAcctException();
        String[] parts = acct.substring(5).split("@");
        if (parts.length != 2) throw new InvalidAcctException();
        if (parts[1].equals(federationService.getDomain())) {
            return getActorById(parts[0]);
        } else {
            try {
                JsonResourceDescriptorDto res = client.get().uri("https://{domain}/.well-known/webfinger?resource={acct}", parts[1], acct).retrieve().body(JsonResourceDescriptorDto.class);
                String url = null;
                for (LinkDto link : res.getLinks()) {
                    if ("self".equals(link.getRel())) {
                        url = link.getHref();
                        break;
                    }
                }
                if (url != null) {
                    Actor actor = new Actor();
                    actor.setId(parts[0]);
                    actor.setDomain(parts[1]);
                    actor.setUrl(url);
                    return actor;
                }
            } catch(Exception e) {
                // :P    
            }
        }
        throw new ActorNotFoundException();
    }

    @Override
    public Actor getActorById(String id) {
        LocalUser user = userRepository.getByUsername(id).orElseThrow(ActorNotFoundException::new);
        Actor actor = new Actor();
        actor.setId(user.getUsername());
        actor.setDomain(federationService.getDomain());
        actor.setUrl(federationService.getBaseUrl() + "/users/" + user.getUsername());
        return actor;
    }

    @Override
    public Actor getActorByUrl(String url) {
        return null;
    }

    @Override
    public Actor getInstanceActor() {
        Actor actor = new Actor();
        actor.setId(federationService.getDomain());
        actor.setDomain(federationService.getDomain());
        actor.setUrl(federationService.getBaseUrl() + "/instance");
        return actor;
    }
    
}
