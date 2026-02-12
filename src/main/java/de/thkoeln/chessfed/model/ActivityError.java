package de.thkoeln.chessfed.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ActivityError {
    
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne
    private Activity activity;
    @ManyToOne
    private Actor target;
    private int errorCounter;

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Actor getTarget() {
        return target;
    }

    public void setTarget(Actor target) {
        this.target = target;
    }

    public int getErrorCounter() {
        return errorCounter;
    }

    public void setErrorCounter(int resendCounter) {
        this.errorCounter = resendCounter;
    }
    
}
