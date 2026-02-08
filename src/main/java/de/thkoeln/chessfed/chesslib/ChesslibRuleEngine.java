package de.thkoeln.chessfed.chesslib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.services.IChessBoardService;
import de.thkoeln.chessfed.services.IRuleEngineService;

@Service
public class ChesslibRuleEngine implements IRuleEngineService {

    private IChessBoardService boardService;
    private Map<UUID, Pair<Board, Boolean>> boardCache = new HashMap<>();

    @Autowired
    public ChesslibRuleEngine(IChessBoardService boardService) {
        this.boardService = boardService;
    }

    @Override
    public ChessPlayer checkMove(ChessGame currentState, ChessMove move) throws InvalidMoveException {
        if (!boardCache.containsKey(currentState.getId())) {
            Board b = new Board();
            String fen = boardService.generateFen(currentState);
            b.loadFromFen(fen);
            boardCache.put(currentState.getId(), Pair.of(b, true));
        }
        Board board = boardCache.get(currentState.getId()).getFirst();
        Square source = Square.fromValue(boardService.getFieldDescriptor(move.getSourceField()).toUpperCase());
        Square target = Square.fromValue(boardService.getFieldDescriptor(move.getTargetField()).toUpperCase());
        Optional<Piece> promote = Optional.ofNullable(move.getPromote()).map(this::convert).map((piece) -> Piece.make(convert(move.getPlayer()), piece));
        Move mv = promote.isEmpty() ? new Move(source, target) : new Move(source, target, promote.get());
        boolean isLegal = board.legalMoves().stream().anyMatch((m) -> m.equals(mv));
        if (!isLegal) {
            throw new InvalidMoveException();
        }
        board.doMove(mv);
        if (!boardCache.get(currentState.getId()).getSecond()) { 
            boardCache.put(currentState.getId(), Pair.of(board, true));
        }
        if (board.isDraw()) {
            return ChessPlayer.NONE;
        } else if (board.isMated()) {
            return move.getPlayer();
        }
        return null;
    }

    private Side convert(ChessPlayer player) {
        switch (player) {
            case WHITE:
                return Side.WHITE;
            case BLACK:
                return Side.BLACK;
            default:
                return null;
        }
    }

    private PieceType convert(ChessPiece piece) {
        switch (piece) {
            case BISHOP:
                return PieceType.BISHOP;
            case KING:
                return PieceType.KING;
            case KNIGHT:
                return PieceType.KNIGHT;
            case PAWN:
                return PieceType.PAWN;
            case QUEEN:
                return PieceType.QUEEN;
            case ROOK:
                return PieceType.ROOK;
            default:
                return null;
        }
    }
    
    @Scheduled(fixedRate = 60000)
    public void clearCache() {
        for (UUID id : new ArrayList<>(boardCache.keySet())) {
            if (boardCache.get(id).getSecond()) {
                Board board = boardCache.get(id).getFirst();
                boardCache.put(id, Pair.of(board, false));
            } else {
                boardCache.remove(id);
            }
        }
    }

}
