package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;

public interface IChessGameService {
    
    byte getFieldFlag(ChessPiece piece, ChessPlayer player);

    ChessPiece getPiece(byte fieldFlag);

    ChessPlayer getPlayer(byte fieldFlag);

    int getFiedlId(String fieldDescriptor);

    String getFieldDescriptor(int fieldId);

}
