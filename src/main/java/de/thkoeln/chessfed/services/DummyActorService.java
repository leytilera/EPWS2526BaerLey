package de.thkoeln.chessfed.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.model.Actor;

@Service
public class DummyActorService implements IActorService {

    private IFederationService federationService;

    @Autowired
    public DummyActorService(IFederationService federationService) {
        this.federationService = federationService;
    }

    @Override
    public Actor getActorByAcct(String acct) {
        String[] parts = acct.substring(5).split("@");
        if (parts.length != 2) throw new RuntimeException("ALEC");
        if (!parts[1].equals(federationService.getDomain())) return null;
        Actor actor = new Actor();
        actor.setId(parts[0]);
        actor.setDomain(parts[1]);
        actor.setUrl(federationService.getBaseUrl() + "/users/" + parts[0]);
        return actor;
    }

    @Override
    public Actor getActorById(String id) {
        return getActorByAcct("acct:" + id + "@" + federationService.getDomain());
    }

    @Override
    public Actor getActorByUrl(String url) {
        return null;
    }
    
}
