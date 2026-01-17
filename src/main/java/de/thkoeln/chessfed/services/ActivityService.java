package de.thkoeln.chessfed.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.thkoeln.chessfed.exception.InvalidActivityException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.ActivityType;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IActivityRepository;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class ActivityService implements IActivityService {

    private IChessGameService gameService;
    private IActorService actorService;
    private IFederationService federationService;
    private IActivityRepository activityRepository;
    private RestClient client = RestClient.create();

    @Autowired
    public ActivityService(IChessGameService gameService, IActorService actorService, IFederationService federationService, IActivityRepository activityRepository) {
        this.gameService = gameService;
        this.actorService = actorService;
        this.federationService = federationService;
        this.activityRepository = activityRepository;
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    @Override
    public void receiveActivity(Actor inboxOwner, Map<String, Object> activityData) {
        if (!activityData.containsKey("type") || !(activityData.get("type") instanceof String)) throw new InvalidActivityException();
        if (!activityData.containsKey("id") || !(activityData.get("id") instanceof String)) throw new InvalidActivityException();
        ActivityType type = ActivityType.parse((String) activityData.get("type"));
        if (type == ActivityType.UNKNOWN) return;
        FederatedObject federatedObject = federationService.createFederatedObject((String) activityData.get("id"), ObjectType.ACTIVITY);
        if (activityRepository.getByFederation(federatedObject).isPresent()) return;
        Activity activity = new Activity();
        activity.setFederation(federatedObject);
        Actor actor = parseActor(activityData.get("actor"));
        activity.setActor(actor);
        activity.setType(type);

        Object object = activityData.get("object");
        if (object instanceof String) {
            //TODO: Remote Request
        } else if (object instanceof Map) {
            activity.setObject(parseObject((Map<String, Object>) object));
        } 

        Object target = activityData.get("target");
        if (target instanceof String) {
            //TODO: Remote Request
        } else if (target instanceof Map) {
            activity.setTarget(new FederatedObject[]{parseObject((Map<String, Object>) target)});
        } else if (target instanceof List) {
            FederatedObject[] arr = ((List<Object>) target).stream()
                .filter((e) -> e instanceof Map)
                .map((e) -> (Map<String, Object>) e)
                .filter((e) -> (e.get("type") instanceof String) || (e.get("id") instanceof String))
                .map(this::parseObject)
                .toArray(FederatedObject[]::new);
            activity.setTarget(arr);
        }
        
        activityRepository.save(activity);

        //TODO: process activity content
    }

    private FederatedObject parseObject(Map<String, Object> ref) {
        if (!(ref.get("type") instanceof String) || !(ref.get("id") instanceof String)) throw new InvalidActivityException();
        ObjectType refType = ObjectType.parse((String) ref.get("type"));
        if (refType == null) throw new InvalidActivityException();
        return federationService.createFederatedObject((String) ref.get("id"), refType);
    }

    private Actor parseActor(Object actorObject) {
        if (actorObject instanceof String) {
            return actorService.getActorByUrl((String) actorObject);
        } else if (actorObject instanceof Map && ((Map) actorObject).get("id") instanceof String) {
            return actorService.getActorByUrl((String)((Map) actorObject).get("id"));
        }
        throw new InvalidActivityException();
    }

    private <T> T fetchRemote(String url, Class<T> type) {
        try {
            return client.get().uri(url).retrieve().body(type);
        } catch (Exception e) {
            throw new InvalidActivityException();
        }
    }
    
}
