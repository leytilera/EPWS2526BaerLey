package de.thkoeln.chessfed.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.IChessGameRepository;
import de.thkoeln.chessfed.model.IChessMoveRepository;

@Service
public class ChessGameService implements IChessGameService {
    
    private IChessGameRepository gameRepository;
    private IChessMoveRepository moveRepository;
    private IRuleEngineService ruleEngine;
    private static char[] rowLookupTable = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
    private static char[] columnLookupTable = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    @Autowired
    public ChessGameService(IChessGameRepository gameRepository, IChessMoveRepository moveRepository) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
    }

    @Override
    public byte getFieldFlag(ChessPiece piece, ChessPlayer player) {
        if (player == ChessPlayer.NONE) return 0;
        int flag = player == ChessPlayer.WHITE ? (piece.ordinal() + 1) << 1 : (piece.ordinal() << 1) + 1;
        return (byte) (flag & 0xFF);
    }

    @Override
    public ChessPiece getPiece(byte fieldFlag) {
        if (fieldFlag == 0) return null;
        int flag = fieldFlag & 0xFF;
        boolean isWhite = (fieldFlag & 1) == 0;
        int pieceId = isWhite ? (flag >>> 1) - 1 : (flag - 1) >>> 1;
        if (pieceId < 0 || pieceId >= ChessPiece.values().length) throw new IllegalArgumentException("ALEC");
        return ChessPiece.values()[pieceId];
    }

    @Override
    public ChessPlayer getPlayer(byte fieldFlag) {
        if (fieldFlag == 0) return ChessPlayer.NONE;
        return (fieldFlag & 1) == 0 ? ChessPlayer.WHITE : ChessPlayer.BLACK;
    }

    @Override
    public int getFieldId(String fieldDescriptor) {
        if (fieldDescriptor == null || fieldDescriptor.chars().count() != 2) throw new IllegalArgumentException("ALEC");
        char[] parts = fieldDescriptor.toCharArray();
        int column = getArrayPosition(columnLookupTable, parts[0]);
        int row = getArrayPosition(rowLookupTable, parts[1]);
        if (column < 0 || row < 0) throw new IllegalArgumentException("ALEC");
        return getFieldId(row, column);
    }

    @Override
    public String getFieldDescriptor(int fieldId) {
        if (fieldId < 0 || fieldId > 63) throw new IllegalArgumentException("ALEC");
        int column = getFieldColumnIndex(fieldId);
        int row = getFieldRowIndex(fieldId);
        String desc = "" + columnLookupTable[column] + rowLookupTable[row];
        return desc;
    }

    private int getArrayPosition(char[] array, char c) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) return i;
        }
        return -1;
    }

    @Override
    public int getFieldRowIndex(int fieldId) {
        return fieldId / 8;
    }

    @Override
    public int getFieldColumnIndex(int fieldId) {
        return fieldId % 8;
    }

    @Override
    public int getFieldId(int rowIndex, int columnIndex) {
        return rowIndex * 8 + columnIndex;
    }

    @Override
    public ChessMove createMove(ChessGame game, int sourceFieldId, int targetFieldId) {
        ChessMove move = new ChessMove();
        move.setGame(game);
        move.setSourceField(sourceFieldId);
        move.setTargetField(targetFieldId);
        move.setMoveCount(game.getMoveCounter() + 1);
        move.setPlayer(game.getCurrentTurn());
        move.setCastle(false);
        move.setCapture(targetFieldId == game.getEnPassentField() || game.getFields()[targetFieldId] != 0);
        return move;
    }

    @Override
    public List<ChessMove> getMoves(ChessGame game) {
        List<ChessMove> moves = moveRepository.getAllByGame(game);
        Collections.sort(moves, (ChessMove m1, ChessMove m2) -> m1.getMoveCount() - m2.getMoveCount());
        return moves;
    }

    @Override
    public void applyMove(ChessMove move) throws InvalidMoveException {
        ChessGame game = ruleEngine.applyMove(move.getGame(), move, this);
        game.setMoveCounter(move.getMoveCount());
        gameRepository.save(game);
        moveRepository.save(move);
    }

    @Override
    public ChessGame createGame(Actor whitePlayer, Actor blackPlayer) {
        ChessGame game = new ChessGame();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setCurrentTurn(ChessPlayer.WHITE);
        game.setMoveCounter(0);
        game.setHasEnded(false);
        game.setCastleState((byte) 0);
        gameRepository.save(game);
        return game;
    }

}
