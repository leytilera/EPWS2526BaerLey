package de.thkoeln.chessfed.services;

import java.util.List;
import java.util.UUID;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.FederatedObject;

public interface IChessGameService {
    
    byte getFieldFlag(ChessPiece piece, ChessPlayer player);

    ChessPiece getPiece(byte fieldFlag);

    ChessPlayer getPlayer(byte fieldFlag);

    int getFieldId(String fieldDescriptor);

    String getFieldDescriptor(int fieldId);

    int getFieldRowIndex(int fieldId);

    int getFieldColumnIndex(int fieldId);

    int getFieldId(int rowIndex, int columnIndex);

    ChessMove createMove(ChessGame game, int sourceFieldId, int targetFieldId);

    List<ChessMove> getMoves(ChessGame game);

    void applyMove(ChessMove move, boolean force) throws InvalidMoveException;

    ChessGame createGame(Actor whitePlayer, Actor blackPlayer);

    void createGame(ChessGame customGame);

    void addRemoteGame(ChessGame game);

    ChessGame getGame(UUID id);

    ChessGame getGame(FederatedObject federatedObject);

    List<ChessGame> getGames(Actor player);

    ChessMove getMove(FederatedObject federatedObject);

    ChessMove getMove(ChessGame game, int count);

}
