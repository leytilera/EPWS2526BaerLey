package de.thkoeln.chessfed.events;

import org.springframework.context.ApplicationEvent;

import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.Challenge;

public class InviteEvent extends ApplicationEvent {

    private Actor source;
    private Actor target;
    private Challenge challenge;

    public InviteEvent(Object source) {
        super(source);
    }

    public Actor getSource() {
        return source;
    }

    public void setSource(Actor source) {
        this.source = source;
    }

    public Actor getTarget() {
        return target;
    }

    public void setTarget(Actor target) {
        this.target = target;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
    
}
