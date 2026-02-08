package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPlayer;

public interface IRuleEngineService {
    
    ChessPlayer checkMove(ChessGame currentState, ChessMove move) throws InvalidMoveException;

}
