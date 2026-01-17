package de.thkoeln.chessfed.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Activity {
    
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne
    private Actor actor;
    @OneToOne
    private FederatedObject federation;
    @ManyToOne
    private FederatedObject object;
    @ManyToMany
    private FederatedObject[] target;
    @Enumerated(EnumType.ORDINAL)
    private ActivityType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public FederatedObject getFederation() {
        return federation;
    }

    public void setFederation(FederatedObject federation) {
        this.federation = federation;
    }

    public FederatedObject getObject() {
        return object;
    }

    public void setObject(FederatedObject object) {
        this.object = object;
    }

    public FederatedObject[] getTarget() {
        return target;
    }

    public void setTarget(FederatedObject[] target) {
        this.target = target;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

}
