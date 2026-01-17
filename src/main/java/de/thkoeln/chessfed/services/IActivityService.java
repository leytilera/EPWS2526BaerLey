package de.thkoeln.chessfed.services;

import java.util.Map;

import de.thkoeln.chessfed.model.Actor;

public interface IActivityService {
    
    void receiveActivity(Actor inboxOwner, Map<String, Object> activityData);

}
