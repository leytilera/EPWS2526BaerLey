package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.LocalUser;

public interface IActorService {

    Actor getActorByAcct(String acct);

    Actor getActorById(String id);

    Actor getActorByUrl(String url);

    Actor getInstanceActor();

    LocalUser createUser(String username);

}
