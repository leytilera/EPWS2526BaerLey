package de.thkoeln.chessfed.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.dto.ActorDto;
import de.thkoeln.chessfed.dto.ApiChallengeDto;
import de.thkoeln.chessfed.dto.ApiGameDto;
import de.thkoeln.chessfed.dto.ApiMoveDto;
import de.thkoeln.chessfed.dto.CastleStateDto;
import de.thkoeln.chessfed.dto.ChallengeDto;
import de.thkoeln.chessfed.dto.GameDto;
import de.thkoeln.chessfed.dto.MoveDto;
import de.thkoeln.chessfed.exception.InvalidActivityException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.FederatedObject;

@Service
public class MappingService implements IMappingService {

    IChessBoardService boardService;
    IChessGameService gameService;

    @Autowired
    public MappingService(IChessBoardService boardService, IChessGameService gameService) {
        this.boardService = boardService;
        this.gameService = gameService;
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    @Override
    public <T> T map(Object obj, Class<T> clazz) {
        if (clazz == ActivityPubDto.class && obj instanceof FederatedObject) {
            return (T) mapFederatedObject((FederatedObject) obj);
        } else if (clazz == ActivityDto.class && obj instanceof Activity) {
            return (T) mapToActivity((Activity) obj);
        } else if (clazz == ApiGameDto.class && obj instanceof ChessGame) {
            return (T) mapToApiGame((ChessGame) obj);
        } else if (clazz == ApiChallengeDto.class && obj instanceof Challenge) {
            return (T) mapToApiChallenge((Challenge) obj);
        } else if (clazz == ApiMoveDto.class && obj instanceof ChessMove) {
            return (T) mapToApiMove((ChessMove) obj);
        } else if (clazz == ActorDto.class && obj instanceof Actor) {
            return (T) mapToActor((Actor) obj);
        } else if (clazz == ChallengeDto.class && obj instanceof Challenge) {
            return (T) mapToChallenge((Challenge) obj);
        } else if (clazz == GameDto.class && obj instanceof ChessGame) {
            return (T) mapToGame((ChessGame) obj);
        } else if (clazz == MoveDto.class && obj instanceof ChessMove) {
            return (T) mapToMove((ChessMove) obj);
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    @Override
    public <T> T parse(Map<String, Object> json, Class<T> clazz) {
        if (clazz == ActivityDto.class) {
            return (T) parseActivity(json);
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings({"unchecked", "ALEC"})
    private ActivityDto parseActivity(Map<String, Object> json) {
        ActivityDto dto = new ActivityDto();
        if (json.get("id") instanceof String) {
            dto.setId((String) json.get("id"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("type") instanceof String) {
            dto.setType((String) json.get("type"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("actor") instanceof Map) {
            Map<String, Object> actor = (Map<String, Object>) json.get("actor");
            dto.setActor(new ActivityPubDto((String) actor.get("id"), (String) actor.get("type")));
        } else if (json.get("actor") instanceof String) {
            dto.setActor(new ActivityPubDto((String) json.get("actor"), "Actor"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("object") instanceof Map) {
            Map<String, Object> object = (Map<String, Object>) json.get("object");
            dto.setObject(new ActivityPubDto((String) object.get("id"), (String) object.get("type")));
        } else if (json.get("object") instanceof String) {
            dto.setObject(new ActivityPubDto((String) json.get("object"), "Object"));
        }
        if (json.get("target") instanceof Map) {
            Map<String, Object> target = (Map<String, Object>) json.get("target");
            dto.setTarget(new ActivityPubDto[]{new ActivityPubDto((String) target.get("id"), (String) target.get("type"))});
        } else if (json.get("target") instanceof String) {
            dto.setTarget(new ActivityPubDto[]{new ActivityPubDto((String) json.get("target"), "Object")});
        } else if (json.get("target") instanceof List) {
            dto.setTarget(((List<Object>) json.get("target")).stream()
                .filter((e) -> e instanceof Map)
                .map((e) -> (Map<String, Object>) e)
                .filter((e) -> e.get("id") instanceof String && e.get("type") instanceof String)
                .map((e) -> new ActivityPubDto((String) e.get("id"), (String) e.get("type")))
                .toArray(ActivityPubDto[]::new)
            );
        }
        return dto;
    }

    private ActivityPubDto mapFederatedObject(FederatedObject object) {
        return new ActivityPubDto(object.getId(), object.getType().toString());
    }

    private ActivityDto mapToActivity(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getFederation().getId());
        dto.setType(activity.getFederation().getType().toString());
        dto.setActor(mapFederatedObject(activity.getActor().getFederation()));
        Optional.ofNullable(activity.getObject())
            .map(this::mapFederatedObject)
            .ifPresent(dto::setObject);
        Optional.ofNullable(activity.getTarget())
            .map(Arrays::stream)
            .map((s) -> s.map(this::mapFederatedObject))
            .map((s) -> s.toArray(ActivityPubDto[]::new))
            .ifPresent(dto::setTarget);
        return dto;
    }

    private ApiGameDto mapToApiGame(ChessGame game) {
        ApiGameDto dto = new ApiGameDto();
        dto.setId(game.getId());
        Actor current = game.getCurrentTurn().get(game.getWhitePlayer(), game.getBlackPlayer());
        dto.setCurrentTurn(current.getId());
        dto.setBlack(game.getBlackPlayer().getId());
        dto.setWhite(game.getWhitePlayer().getId());
        CastleStateDto castleState = new CastleStateDto();
        castleState.setWhiteShort(game.getCastleState().isWhiteShort());
        castleState.setWhiteLong(game.getCastleState().isWhiteLong());
        castleState.setBlackShort(game.getCastleState().isBlackShort());
        castleState.setBlackLong(game.getCastleState().isBlackLong());
        dto.setCastleState(castleState);
        if (game.getEnPassentField() >= 0) dto.setEnPassantField(boardService.getFieldDescriptor(game.getEnPassentField()));
        String[][] board = new String[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int field = boardService.getFieldIndex(i, j);
                ChessPiece piece = ChessPiece.fromField(game.getFields()[field]);
                ChessPlayer player = ChessPlayer.fromField(game.getFields()[field]);
                board[i][j] = Optional.ofNullable(piece).map((p) -> p.getAbbrev(player)).orElse(null);
            }
        }
        dto.setBoard(board);
        return dto;
    }

    private ApiChallengeDto mapToApiChallenge(Challenge challenge) {
        ApiChallengeDto dto = new ApiChallengeDto();
        dto.setId(challenge.getId());
        dto.setWhite(Optional.ofNullable(challenge.getWhite()).map((a) -> a.getFederation().getId()).orElse(null));
        Actor source = challenge.getInvitation().getActor();
        dto.setSource(source.getFederation().getId());
        dto.setSourceHandle(source.getLocalpart() + "@" + source.getDomain());
        Actor target = challenge.getInvited();
        dto.setTarget(target.getFederation().getId());
        dto.setTargetHandle(target.getLocalpart() + "@" + target.getDomain());
        return dto;
    }

    private ApiMoveDto mapToApiMove(ChessMove move) {
        ApiMoveDto dto = new ApiMoveDto();
        dto.setSource(boardService.getFieldDescriptor(move.getSourceField()));
        dto.setTarget(boardService.getFieldDescriptor(move.getTargetField()));
        Optional.ofNullable(move.getPromote()).map(ChessPiece::getAbbrev).ifPresent(dto::setPromote);
        dto.setCapture(move.isCapture());
        dto.setCastle(move.isCastle());
        return dto;
    }

    private ActorDto mapToActor(Actor actor) {
        return new ActorDto(
            actor.getFederation().getId(), 
            actor.getFederation().getType().toString(), 
            actor.getLocalpart(), 
            actor.getLocalpart(), 
            actor.getInbox(),
            actor.getOutbox()
        );
    }

    private ChallengeDto mapToChallenge(Challenge challenge) {
        ChallengeDto dto = new ChallengeDto();
        dto.setId(challenge.getFederation().getId());
        if (challenge.getWhite() != null) {
            dto.setWhite(challenge.getWhite().getFederation().getId());
        }
        return dto;
    }

    private GameDto mapToGame(ChessGame chessGame) {
        List<ChessMove> moves = gameService.getMoves(chessGame);
        GameDto dto = new GameDto();
        dto.setId(chessGame.getFederation().getId());
        dto.setPublished(null);
        dto.setWhite(chessGame.getWhitePlayer().getId());
        dto.setBlack(chessGame.getBlackPlayer().getId());
        dto.setFinished(chessGame.isHasEnded());
        dto.setWinner(!chessGame.isHasEnded() || chessGame.getCurrentTurn() == ChessPlayer.NONE ? null : (chessGame.getCurrentTurn() == ChessPlayer.WHITE) ? chessGame.getWhitePlayer().getId() : chessGame.getBlackPlayer().getId());
        dto.setCurrentTurn(chessGame.getCurrentTurn() == ChessPlayer.WHITE ? chessGame.getWhitePlayer().getId() : chessGame.getBlackPlayer().getId());
        String[][] board = new String[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int field = boardService.getFieldIndex(i, j);
                ChessPiece piece = ChessPiece.fromField(chessGame.getFields()[field]);
                ChessPlayer player = ChessPlayer.fromField(chessGame.getFields()[field]);
                board[i][j] = Optional.ofNullable(piece).map((p) -> p.getAbbrev(player)).orElse(null);
            }
        }
        dto.setBoard(board);
        if (chessGame.getEnPassentField() >= 0) dto.setEnPassantField(boardService.getFieldDescriptor(chessGame.getEnPassentField()));
        CastleStateDto castleState = new CastleStateDto();
        castleState.setWhiteShort(chessGame.getCastleState().isWhiteShort());
        castleState.setWhiteLong(chessGame.getCastleState().isWhiteLong());
        castleState.setBlackShort(chessGame.getCastleState().isBlackShort());
        castleState.setBlackLong(chessGame.getCastleState().isBlackLong());
        dto.setCastleState(castleState);
        dto.setTotalItems(moves.size());
        ActivityPubDto[] moveRef = moves.stream()
            .map(ChessMove::getFederation)
            .map(this::mapFederatedObject)
            .toArray(ActivityPubDto[]::new);
        dto.setItems(moveRef);
        return dto;
    }

    private MoveDto mapToMove(ChessMove move) {
        MoveDto dto = new MoveDto();
        dto.setId(move.getFederation().getId());
        dto.setPublished(null);
        dto.setSource(boardService.getFieldDescriptor(move.getSourceField()));
        dto.setTarget(boardService.getFieldDescriptor(move.getTargetField()));
        dto.setCapture(move.isCapture());
        dto.setCastle(move.isCastle());
        dto.setPromote(Optional.ofNullable(move.getPromote()).map((p) -> p.getAbbrev(move.getPlayer())).orElse(null));
        return dto;
    }

}
