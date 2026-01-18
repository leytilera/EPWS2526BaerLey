package de.thkoeln.chessfed.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.exception.InvalidActivityException;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.ActivityType;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IActivityRepository;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class ActivityService implements IActivityService {

    static MediaType activityType = MediaType.valueOf("application/activity+json");

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

    @Override
    public void postActivity(Activity activity) {
        activity.setFederation(federationService.createFederatedObject(activity.getId(), ObjectType.ACTIVITY));
        activityRepository.save(activity);
        Set<Actor> targets = getTargetActors(activity);
        broadcastActivity(activity, targets);
    }

    private void broadcastActivity(Activity activity, Set<Actor> targets) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getFederation().getId());
        dto.setType(activity.getType().toString());
        if (activity.getActor() != null) {
            dto.setActor(new ActivityPubDto(activity.getActor().getFederation().getId(), "Person"));
        }
        if (activity.getObject() != null) {
            dto.setObject(new ActivityPubDto(activity.getObject().getId(), activity.getObject().getType().toString()));
        }
        if (activity.getTarget() != null) {
            ActivityPubDto[] target = Arrays.stream(activity.getTarget())
                .map((o) -> new ActivityPubDto(o.getId(), o.getType().toString()))
                .toArray(ActivityPubDto[]::new);   
            dto.setTarget(target);     
        }
        for (Actor actor : targets) {
            if (actor.getDomain().equals(federationService.getDomain())) continue;
            boolean success = client.post()
                .uri(actor.getInbox())
                .contentType(activityType)
                .body(dto)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode()
                .is2xxSuccessful();
            if (!success) {
                //TODO: handle errors
                System.out.println("ERROR!");
            }
        }
    }

    private Set<Actor> getTargetActors(Activity activity) {
        Set<Actor> targets = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Queue<Object> toVisit = new LinkedList<>();
        BiConsumer<Object, String> addToVisit = (obj, id) -> {
            if (!visited.contains(id)) toVisit.add(obj);
            visited.add(id);
        };
        addToVisit.accept(activity, activity.getFederation().getId());
        while (!toVisit.isEmpty()) {
            Object obj = toVisit.poll();
            if (obj instanceof Actor) {
                Actor actor = (Actor) obj;
                if (actor.getFederation().getId().equals(activity.getActor().getFederation().getId())) continue;
                targets.add(actor);
            } else if (obj instanceof Activity) {
                Activity act = (Activity) obj;
                addToVisit.accept(act.getActor(), act.getActor().getFederation().getId());
                if (act.getObject() != null) {
                    addToVisit.accept(act.getObject(), act.getObject().getId());
                }
                if (act.getTarget() != null) {
                    for (FederatedObject target : act.getTarget()) {
                        addToVisit.accept(target, target.getId());
                    }
                }
                visited.add(act.getFederation().getId());
            } else if (obj instanceof ChessGame) {
                ChessGame game = (ChessGame) obj;
                addToVisit.accept(game.getWhitePlayer(), game.getWhitePlayer().getFederation().getId());
                addToVisit.accept(game.getBlackPlayer(), game.getBlackPlayer().getFederation().getId());

            } else if (obj instanceof ChessMove) {
                ChessMove move = (ChessMove) obj;
                addToVisit.accept(move.getGame(), move.getGame().getFederation().getId());
            } else if (obj instanceof FederatedObject) {
                FederatedObject fed = (FederatedObject) obj;
                try {
                    switch (fed.getType()) {
                        case ACTIVITY: {
                            activityRepository.getByFederation(fed).ifPresent(toVisit::add);
                        } break;
                        case ACTOR: {
                            Actor actor = actorService.getActorByUrl(fed.getId());
                            toVisit.add(actor);
                        } break;
                        case GAME: {
                            ChessGame game = gameService.getGame(fed);
                            toVisit.add(game);
                        } break;
                        case MOVE: {
                            ChessMove move = gameService.getMove(fed);
                            toVisit.add(move);
                        } break;
                    }
                } catch (ResourceNotFoundException e) {

                }
            }
        }
        return targets;
    }
    
}
