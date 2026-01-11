package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.model.Actor;

public interface IActorService {

    Actor getActorByAcct(String acct);

    Actor getActorById(String id);

    Actor getActorByUrl(String url);

}
