package de.thkoeln.chessfed.services;

import java.util.List;
import java.util.Map;

import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ObjectType;

public interface IActivityService {
    
    void receiveActivity(Actor inboxOwner, Map<String, Object> activityData);

    void postActivity(Activity activity);

    Activity createActivity(Actor actor, ObjectType type);

    List<Activity> getOutbox(Actor outboxOwner);

}
