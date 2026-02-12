package de.thkoeln.chessfed.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.dto.ChallengeDto;
import de.thkoeln.chessfed.dto.CollectionDto;
import de.thkoeln.chessfed.dto.GameDto;
import de.thkoeln.chessfed.dto.MoveDto;
import de.thkoeln.chessfed.events.GameCreationEvent;
import de.thkoeln.chessfed.events.InviteEvent;
import de.thkoeln.chessfed.events.MoveEvent;
import de.thkoeln.chessfed.exception.InvalidActivityException;
import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.CastleState;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IActivityRepository;
import de.thkoeln.chessfed.model.IChallengeRepository;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class ActivityService implements IActivityService {

    static MediaType activityType = MediaType.valueOf("application/activity+json");

    private IChessGameService gameService;
    private IActorService actorService;
    private IFederationService federationService;
    private IActivityRepository activityRepository;
    private IChallengeRepository challengeRepository;
    private ApplicationEventPublisher eventBus;
    private IMappingService mappingService;
    private RestClient client = RestClient.create();

    @Autowired
    public ActivityService(IChessGameService gameService, IActorService actorService, IFederationService federationService, IActivityRepository activityRepository, IChallengeRepository challengeRepository, ApplicationEventPublisher eventBus, IMappingService mappingService) {
        this.gameService = gameService;
        this.actorService = actorService;
        this.federationService = federationService;
        this.activityRepository = activityRepository;
        this.challengeRepository = challengeRepository;
        this.eventBus = eventBus;
        this.mappingService = mappingService;
    }

    @Override
    public void receiveActivity(Actor inboxOwner, Map<String, Object> activityData) {
        ActivityDto dto = mappingService.parse(activityData, ActivityDto.class);
        ObjectType type = ObjectType.parse(dto.getType());
        if (type == ObjectType.UNKNOWN) return;
        FederatedObject federatedObject = federationService.createFederatedObject(dto.getId(), type);
        if (activityRepository.getByFederation(federatedObject).isPresent()) return;
        Activity activity = new Activity();
        activity.setFederation(federatedObject);
        Actor actor = parseActor(dto.getActor());
        activity.setActor(actor);

        ActivityPubDto object = dto.getObject();
        if (object != null) {
            activity.setObject(parseObject(object));
        } 

        ActivityPubDto[] target = dto.getTarget();
        if (target != null) {
            FederatedObject[] arr = Arrays.stream(target)
                .map(this::parseObject)
                .toArray(FederatedObject[]::new);
            activity.setTarget(arr);
        }
        
        activityRepository.save(activity);
        processActivity(activity);
    }

    private FederatedObject parseObject(ActivityPubDto ref) {
        ObjectType refType = ObjectType.parse(ref.getType());
        if (refType == ObjectType.UNKNOWN) {
            ActivityPubDto dto = fetchRemote(ref.getId(), ActivityPubDto.class);
            return federationService.createFederatedObject(dto.getId(), ObjectType.parse(dto.getType()));
        }
        return federationService.createFederatedObject(ref.getId(), refType);
    }

    private Actor parseActor(ActivityPubDto actorObject) {
        return actorService.getActorByUrl(actorObject.getId());
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
        if (activity.getFederation() == null) throw new IllegalArgumentException();
        if (activityRepository.getByFederation(activity.getFederation()).isEmpty()) {
            activityRepository.save(activity);
            processActivity(activity);
        }
        Set<Actor> targets = getTargetActors(activity);
        broadcastActivity(activity, targets);
    }

    private void broadcastActivity(Activity activity, Set<Actor> targets) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getFederation().getId());
        dto.setType(activity.getFederation().getType().toString());
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
            if (federationService.isLocal(actor.getFederation())) continue;
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

    @SuppressWarnings({"incomplete-switch", "ALEC"})
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
                    switch (fed.getType().simplify()) {
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

    @SuppressWarnings({"incomplete-switch", "ALEC"})
    private void processActivity(Activity activity) {
        switch (activity.getFederation().getType()) {
            case ACCEPT: {
                processAccept(activity);
            } break;
            case CREATE: {
                if (activity.getObject() == null || activity.getObject().getType() != ObjectType.GAME) {
                    break;
                }
                processCreateGame(activity);
            } break;
            case INVITE: {
                if (activity.getObject() == null || activity.getObject().getType() != ObjectType.CHALLENGE) {
                    break;
                } else if (activity.getTarget() == null || activity.getTarget().length != 1 || activity.getTarget()[0].getType().simplify() != ObjectType.ACTOR) {
                    break;
                }
                processInvite(activity);
            } break;
            case PLAY: {
                if (activity.getObject() == null || activity.getObject().getType() != ObjectType.MOVE) {
                    break;
                } else if (activity.getTarget() == null || activity.getTarget().length != 1 || activity.getTarget()[0].getType() != ObjectType.GAME) {
                    break;
                }
                processPlayMove(activity);
            } break;
        }
    }

    private void processCreateGame(Activity create) {
        FederatedObject gme = create.getObject();
        ChessGame game;
        try {
            game = gameService.getGame(gme);
        } catch (ResourceNotFoundException e) {
            game = fetchGame(gme);
            gameService.addRemoteGame(game);
            GameCreationEvent event = new GameCreationEvent(this, game);
            eventBus.publishEvent(event);
        }
        final ChessGame g = game;
        Arrays.stream(new Actor[]{game.getWhitePlayer(), game.getBlackPlayer()})
            .filter((a) -> federationService.isLocal(a.getFederation()))
            .forEach((a) -> joinGame(a, g));
    }

    private void processInvite(Activity invite) {
        FederatedObject ch = invite.getObject();
        Challenge challenge;
        try {
            challenge = challengeRepository.getByFederation(ch).orElseThrow(ResourceNotFoundException::new);
        } catch (ResourceNotFoundException e) {
            ChallengeDto dto = fetchRemote(ch.getId(), ChallengeDto.class);
            challenge = new Challenge();
            challenge.setFederation(ch);
            challenge.setAccepted(false);
            challenge.setInvitation(invite);
            Actor invited = actorService.getActorByUrl(invite.getTarget()[0].getId());
            challenge.setInvited(invited);
            if (dto.getWhite() != null) challenge.setWhite(actorService.getActorByUrl(dto.getWhite()));
            challengeRepository.save(challenge);
        }
        InviteEvent event = new InviteEvent(this);
        event.setChallenge(challenge);
        event.setSource(invite.getActor());
        event.setTarget(challenge.getInvited());
        eventBus.publishEvent(event);
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    private void processAccept(Activity accept) {
        FederatedObject inv = accept.getObject();
        Activity invite;
        try {
            invite = activityRepository.getByFederation(inv).orElseThrow(ResourceNotFoundException::new);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> json = fetchRemote(inv.getId(), Map.class);
            ActivityDto dto = mappingService.parse(json, ActivityDto.class);
            invite = new Activity();
            invite.setFederation(federationService.createFederatedObject(dto.getId(), ObjectType.parse(dto.getType())));
            invite.setActor(actorService.getActorByUrl(dto.getActor().getId()));
            if (dto.getObject() != null) {
                invite.setObject(federationService.createFederatedObject(dto.getObject().getId(), ObjectType.parse(dto.getObject().getType())));
            }
            if (dto.getTarget() != null) {
                invite.setTarget(Arrays.stream(dto.getTarget())
                    .map((t) -> federationService.createFederatedObject(t.getId(), ObjectType.parse(t.getType())))
                    .toArray(FederatedObject[]::new)
                );
            }
            activityRepository.save(invite);
        }
        Challenge challenge = challengeRepository.getByFederation(invite.getObject()).orElseThrow(ResourceNotFoundException::new);
        challenge.setAccepted(true);
        challengeRepository.save(challenge);
        if (federationService.isLocal(challenge.getFederation())) {
            createGameFromChallenge(challenge);
        }
    }

    private void processPlayMove(Activity play) {
        FederatedObject mv = play.getObject();
        FederatedObject gme = play.getTarget()[0];
        ChessGame game;
        try {
            game = gameService.getGame(gme);
        } catch (ResourceNotFoundException e) {
            throw new InvalidActivityException();
        }
        boolean isLocal = federationService.isLocal(game.getFederation());
        try {
            gameService.getMove(mv);
            return; // Move already processed
        } catch (ResourceNotFoundException e) {
            // Continue processing move
        }
        MoveDto dto = fetchRemote(mv.getId(), MoveDto.class);
        ChessMove move = gameService.createMove(game, gameService.getFieldId(dto.getSource()), gameService.getFieldId(dto.getTarget()));
        move.setFederation(mv);
        move.setCapture(dto.isCapture());
        move.setCastle(dto.isCastle());
        if (dto.getPromote() != null) {
            move.setPromote(ChessPiece.parse(dto.getPromote()));
        }
        try {
            gameService.applyMove(move, false);// !isLocal);
        } catch (InvalidMoveException e) {
            if (isLocal) {
                throw new InvalidActivityException(e);
            } else {
                UUID gameId = game.getId();
                game = fetchGame(gme);
                game.setId(gameId);
                gameService.addRemoteGame(game);
            }
        }
        MoveEvent event = new MoveEvent(this);
        event.setGame(game);
        event.setSource(dto.getSource());
        event.setTarget(dto.getTarget());
        event.setPromote(dto.getPromote());
        event.setCapture(move.isCapture());
        event.setCastle(move.isCastle());
        event.setPlayer(play.getActor());
        event.setOpponent(move.getPlayer() == ChessPlayer.WHITE ? game.getBlackPlayer() : game.getWhitePlayer());
        eventBus.publishEvent(event);
    }

    private ChessGame fetchGame(FederatedObject gme) {
        GameDto dto = fetchRemote(gme.getId(), GameDto.class);
        ChessGame game = new ChessGame();
        game.setFederation(gme);
        game.setWhitePlayer(actorService.getActorByUrl(dto.getWhite()));
        game.setBlackPlayer(actorService.getActorByUrl(dto.getBlack()));
        game.setHasEnded(dto.isFinished());
        game.setMoveCounter(dto.getTotalItems());
        game.setCastleState(new CastleState(
            dto.getCastleState().isWhiteShort(), 
            dto.getCastleState().isWhiteLong(),
            dto.getCastleState().isBlackShort(),
            dto.getCastleState().isBlackLong()
        ));
        byte[] fields = new byte[64];
        for (int i = 0; i < dto.getBoard().length; i++) {
            for (int j = 0; j < dto.getBoard()[i].length; j++) {
                fields[gameService.getFieldId(i, j)] = gameService.getFieldFlag(ChessPiece.parse(dto.getBoard()[i][j]), ChessPlayer.parse(dto.getBoard()[i][j]));
            }
        }
        game.setFields(fields);
        if (dto.getEnPassantField() != null) game.setEnPassentField(gameService.getFieldId(dto.getEnPassantField()));
        game.setCurrentTurn(getPlayerFromActor(dto.isFinished() ? actorService.getActorByUrl(dto.getWinner()) : actorService.getActorByUrl(dto.getCurrentTurn()), game));
        return game;
    }

    private void createGameFromChallenge(Challenge challenge) {
        Actor white;
        Actor black;
        if (challenge.getWhite() != null) {
            white = challenge.getWhite();
            black = white.getId().equals(challenge.getInvited().getId()) ? challenge.getInvitation().getActor() : challenge.getInvited();
        } else if (Math.random() > 0.5) {
            white = challenge.getInvited();
            black = challenge.getInvitation().getActor();
        } else {
            white = challenge.getInvitation().getActor();
            black = challenge.getInvited();
        }
        ChessGame game = gameService.createGame(white, black);
        Activity create = createActivity(actorService.getInstanceActor(), ObjectType.CREATE);
        create.setObject(game.getFederation());
        postActivity(create);
        GameCreationEvent event = new GameCreationEvent(this, game);
        eventBus.publishEvent(event);
    }

    private ChessPlayer getPlayerFromActor(Actor actor, ChessGame game) {
        if (actor == null) {
            return ChessPlayer.NONE;
        } else if (actor.getId().equals(game.getWhitePlayer().getId())) {
            return ChessPlayer.WHITE;
        } else if (actor.getId().equals(game.getBlackPlayer().getId())) {
            return ChessPlayer.BLACK;
        } else {
            return ChessPlayer.NONE;
        }
    }

    @Override
    public Activity createActivity(Actor actor, ObjectType type) {
        Activity activity = new Activity();
        activity.setActor(actor);
        activity.setFederation(federationService.createFederatedObject(activity.getId(), type));
        return activity;
    }

    private void joinGame(Actor user, ChessGame game) {
        Activity join = createActivity(user, ObjectType.JOIN);
        join.setObject(game.getFederation());
        postActivity(join);
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    private void fetchOutbox(Actor outboxOwner) {
        if (federationService.isLocal(outboxOwner.getFederation())) return;
        CollectionDto dto = fetchRemote(outboxOwner.getOutbox(), CollectionDto.class);
        for (ActivityPubDto item : dto.getItems()) {
            try {
                Map<String, Object> activity = fetchRemote(item.getId(), Map.class);
                receiveActivity(null, activity);
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Override
    public List<Activity> getOutbox(Actor outboxOwner) {
        try {
            fetchOutbox(outboxOwner);
        } catch (Exception e) {
            
        }
        return activityRepository.getAllByActor(outboxOwner)
            .stream()
            .filter((a) -> a.getFederation().getType() == ObjectType.JOIN)
            .collect(Collectors.toList());
    }
    
}
