package de.thkoeln.chessfed.events;

import org.springframework.context.ApplicationEvent;

import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ChessGame;

public class MoveEvent extends ApplicationEvent {

    private String source;
    private String target;
    private String promote;
    private boolean castle;
    private boolean capture;
    private ChessGame game;
    private Actor player;
    private Actor opponent;

    public MoveEvent(Object source) {
        super(source);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public boolean isCastle() {
        return castle;
    }

    public void setCastle(boolean castle) {
        this.castle = castle;
    }

    public boolean isCapture() {
        return capture;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public Actor getPlayer() {
        return player;
    }

    public void setPlayer(Actor player) {
        this.player = player;
    }

    public Actor getOpponent() {
        return opponent;
    }

    public void setOpponent(Actor opponent) {
        this.opponent = opponent;
    }
    
}
