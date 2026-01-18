package de.thkoeln.chessfed.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.ActivityType;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IChallengeRepository;
import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class UserInteractionService implements IUserInteractionService {

    private IChessGameService gameService;
    private IActivityService activityService;
    private IActorService actorService;
    private ILocalUserRepository userRepository;
    private IFederationService federationService;
    private IChallengeRepository challengeRepository;

    @Autowired
    public UserInteractionService(IChessGameService gameService, IActivityService activityService, IActorService actorService,
            ILocalUserRepository userRepository, IFederationService federationService, IChallengeRepository challengeRepository) {
        this.gameService = gameService;
        this.activityService = activityService;
        this.actorService = actorService;
        this.userRepository = userRepository;
        this.federationService = federationService;
        this.challengeRepository = challengeRepository;
    }

    @Override
    public void createInvitation(LocalUser user, String opponent) {
        if (!opponent.startsWith("acct:")) opponent = "acct:" + opponent;
        Actor opp = actorService.getActorByAcct(opponent);
        Challenge challenge = new Challenge();
        challenge.setFederation(federationService.createFederatedObject(challenge.getId(), ObjectType.CHALLENGE));
        challenge.setWhite(null);
        challenge.setAccepted(false);
        challenge.setInvitation(null);
        challenge.setInvited(opp);
        challengeRepository.save(challenge);
        Activity invitation = new Activity();
        invitation.setType(ActivityType.INVITE);
        invitation.setActor(user.getActor());
        invitation.setObject(challenge.getFederation());
        invitation.setTarget(new FederatedObject[]{opp.getFederation()});
        activityService.postActivity(invitation);
        challenge.setInvitation(invitation);
        challengeRepository.save(challenge);

    }

    @Override
    public void playMove(LocalUser user, UUID gameId, String sourceField, String targetField, String promote) {
        Actor actor = user.getActor();
        ChessGame game = gameService.getGame(gameId);
        int source = gameService.getFieldId(sourceField);
        int target = gameService.getFieldId(targetField);
        ChessPlayer player = game.getWhitePlayer().getId().equals(actor.getId()) ? ChessPlayer.WHITE : ChessPlayer.BLACK;
        if (game.getCurrentTurn() != player) throw new RuntimeException("Not your turn!");
        ChessMove move = gameService.createMove(game, source, target);
        ChessPiece prom = ChessPiece.parse(promote);
        if (prom != null) {
            move.setPromote(prom);
        }
        try {
            gameService.applyMove(move);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        Activity activity = new Activity();
        activity.setType(ActivityType.PLAY);
        activity.setActor(actor);
        activity.setObject(move.getFederation());
        activity.setTarget(new FederatedObject[]{game.getFederation()});
        activityService.postActivity(activity);
    }

    @Override
    public void acceptInvitation(LocalUser user, UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(ResourceNotFoundException::new);
        Activity invitation = challenge.getInvitation();
        challenge.setAccepted(true);
        challengeRepository.save(challenge);
        Activity accept = new Activity();
        accept.setType(ActivityType.ACCEPT);
        accept.setActor(user.getActor());
        accept.setObject(invitation.getFederation());
        accept.setTarget(invitation.getTarget().clone());
        activityService.postActivity(accept);
    }

    @Override
    public UUID[] getGames(LocalUser user) {
        return gameService.getGames(user.getActor()).stream().map((g) -> g.getId()).toArray(UUID[]::new);
    }

    @Override
    public UUID[] getOpenChallenges(LocalUser user) {
        return challengeRepository.findAll().stream()
            .filter((c) -> !c.isAccepted())
            .filter((c) -> c.getInvited() != null)
            .filter((c) -> c.getInvited().getFederation().getId().equals(user.getActor().getFederation().getId()))
            .map((c) -> c.getId())
            .toArray(UUID[]::new);
    }
    
}
