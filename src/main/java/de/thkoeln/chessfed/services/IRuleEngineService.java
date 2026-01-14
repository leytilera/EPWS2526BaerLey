package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;

public interface IRuleEngineService {
    
    ChessGame applyMove(ChessGame currentState, ChessMove move, IChessGameService gameService) throws InvalidMoveException;

}
