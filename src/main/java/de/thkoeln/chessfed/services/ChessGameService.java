package de.thkoeln.chessfed.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.IChessGameRepository;
import de.thkoeln.chessfed.model.IChessMoveRepository;

@Service
public class ChessGameService implements IChessGameService {
    
    private IChessGameRepository gameRepository;
    private IChessMoveRepository moveRepository;
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
    public int getFiedlId(String fieldDescriptor) {
        if (fieldDescriptor == null || fieldDescriptor.chars().count() != 2) throw new IllegalArgumentException("ALEC");
        char[] parts = fieldDescriptor.toCharArray();
        int column = getArrayPosition(columnLookupTable, parts[0]);
        int row = getArrayPosition(rowLookupTable, parts[1]);
        if (column < 0 || row < 0) throw new IllegalArgumentException("ALEC");
        return row * 8 + column;
    }

    @Override
    public String getFieldDescriptor(int fieldId) {
        if (fieldId < 0 || fieldId > 63) throw new IllegalArgumentException("ALEC");
        int column = fieldId % 8;
        int row = fieldId / 8;
        String desc = "" + columnLookupTable[column] + rowLookupTable[row];
        return desc;
    }

    private int getArrayPosition(char[] array, char c) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) return i;
        }
        return -1;
    }

}
