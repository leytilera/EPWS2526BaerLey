package de.thkoeln.chessfed.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.Utils;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.dto.CastleStateDto;
import de.thkoeln.chessfed.dto.GameDto;
import de.thkoeln.chessfed.dto.MoveDto;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.IChessMoveRepository;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IChessGameService;

@RestController
public class GameController {

    private IChessGameService gameService;
    private IActorService actorService;
    private IChessMoveRepository moveRepository;
    
    @Autowired
    public GameController(IChessGameService gameService, IActorService actorService,
            IChessMoveRepository moveRepository) {
        this.gameService = gameService;
        this.actorService = actorService;
        this.moveRepository = moveRepository;
    }

    @GetMapping(value = "/games/{id}", produces = "application/activity+json")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID id) {
        ChessGame chessGame = gameService.getGame(id);
        List<ChessMove> moves = gameService.getMoves(chessGame);
        GameDto game = new GameDto();
        game.setId(chessGame.getFederation().getId());
        game.setPublished(null);
        game.setWhite(chessGame.getWhitePlayer().getId());
        game.setBlack(chessGame.getBlackPlayer().getId());
        game.setFinished(chessGame.isHasEnded());
        game.setWinner(!chessGame.isHasEnded() || chessGame.getCurrentTurn() == ChessPlayer.NONE ? null : (chessGame.getCurrentTurn() == ChessPlayer.WHITE) ? chessGame.getWhitePlayer().getId() : chessGame.getBlackPlayer().getId());
        game.setCurrentTurn(chessGame.getCurrentTurn() == ChessPlayer.WHITE ? chessGame.getWhitePlayer().getId() : chessGame.getBlackPlayer().getId());
        String[][] board = new String[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int field = gameService.getFieldId(i, j);
                ChessPiece piece = gameService.getPiece(chessGame.getFields()[field]);
                ChessPlayer player = gameService.getPlayer(chessGame.getFields()[field]);
                board[i][j] = Utils.nullableAction(piece, (p) -> p.getAbbrev(player));
            }
        }
        game.setBoard(board);
        if (chessGame.getEnPassentField() >= 0) game.setEnPassantField(gameService.getFieldDescriptor(chessGame.getEnPassentField()));
        game.setCastleState(new CastleStateDto());
        game.setTotalItems(moves.size());
        ActivityPubDto[] moveRef = new ActivityPubDto[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            ChessMove move = moves.get(i);
            moveRef[i] = new ActivityPubDto(move.getFederation().getId(), "chessfed:Move");
        }
        game.setItems(moveRef);
        return ResponseEntity.ok(game);
    }

    @GetMapping(value = "/games/{id}/moves/{moveCount}", produces = "application/activity+json")
    public ResponseEntity<MoveDto> getMove(@PathVariable UUID id, @PathVariable int moveCount) {
        ChessGame game = gameService.getGame(id);
        ChessMove move = gameService.getMove(game, moveCount);
        MoveDto dto = new MoveDto();
        dto.setId(move.getFederation().getId());
        dto.setPublished(null);
        dto.setSource(gameService.getFieldDescriptor(move.getSourceField()));
        dto.setTarget(gameService.getFieldDescriptor(move.getTargetField()));
        dto.setCapture(move.isCapture());
        dto.setCastle(move.isCastle());
        dto.setPromote(Utils.nullableAction(move.getPromote(), (p) -> p.getAbbrev(move.getPlayer())));
        return ResponseEntity.ok(dto);
    }
    
}
