package de.thkoeln.chessfed.services;

import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;

@Service
public class DummyRuleEngineService implements IRuleEngineService {

    @Override
    public ChessGame applyMove(ChessGame currentState, ChessMove move, IChessGameService gameService) throws InvalidMoveException {
        byte[] fields = currentState.getFields();
        if (currentState.getCurrentTurn() != move.getPlayer()) throw new InvalidMoveException();
        if (gameService.getPlayer(fields[move.getSourceField()]) != move.getPlayer()) throw new InvalidMoveException();
        if (gameService.getPlayer(fields[move.getTargetField()]) == move.getPlayer()) throw new InvalidMoveException();
        if (!move.isCapture() && fields[move.getTargetField()] != 0) throw new InvalidMoveException();
        // Move the actual Piece
        fields[move.getTargetField()] = fields[move.getSourceField()];
        fields[move.getSourceField()] = 0;
        // Process En Passant
        int row = gameService.getFieldRowIndex(move.getTargetField());
        int column = gameService.getFieldColumnIndex(move.getTargetField());
        if (move.isCapture() && move.getTargetField() == currentState.getEnPassentField()) {    
            int captureRow = row < 4 ? 3 : 4;
            int captureField = gameService.getFieldId(captureRow, column);
            fields[captureField] = 0;
            currentState.setEnPassentField(-1);
        } else if (gameService.getPiece(fields[move.getTargetField()]) == ChessPiece.PAWN && (row == 3 || row == 4) && (gameService.getFieldRowIndex(move.getSourceField()) == 1 || gameService.getFieldRowIndex(move.getSourceField()) == 6)) {
            int enPassantRow = row == 3 ? 2 : 5;
            int enPassantField = gameService.getFieldId(enPassantRow, column);
            currentState.setEnPassentField(enPassantField);
        } else {
            currentState.setEnPassentField(-1);
        }
        // Castleing
        if ((move.getSourceField() == 4 || move.getSourceField() == 60) && gameService.getPiece(fields[move.getTargetField()]) == ChessPiece.KING) {
            if (move.getTargetField() == 1) {
                fields[2] = fields[0];
                fields[0] = 0;
            } else if (move.getTargetField() == 6) {
                fields[5] = fields[7];
                fields[7] = 0;
            } else if (move.getTargetField() == 62) {
                fields[61] = fields[63];
                fields[63] = 0;
            } else if (move.getTargetField() == 57) {
                fields[58] = fields[56];
                fields[56] = 0;
            }
        }
        // Set turn for next player
        currentState.setCurrentTurn(move.getPlayer().getOpponent());
        return currentState;
    }
    
}
