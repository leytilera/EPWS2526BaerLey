package de.thkoeln.chessfed.events;

import org.springframework.context.ApplicationEvent;

import de.thkoeln.chessfed.model.ChessGame;

public class GameCreationEvent extends ApplicationEvent {

    private ChessGame game;

    public GameCreationEvent(Object source, ChessGame game) {
        super(source);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
    
}
