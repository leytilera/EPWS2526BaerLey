package de.thkoeln.chessfed.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Challenge {
    
    @Id
    private UUID id = UUID.randomUUID();
    @OneToOne
    private FederatedObject federation;
    @ManyToOne
    private Actor white;
    @OneToOne
    private Activity invitation;
    boolean accepted;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FederatedObject getFederation() {
        return federation;
    }

    public void setFederation(FederatedObject federation) {
        this.federation = federation;
    }

    public Actor getWhite() {
        return white;
    }

    public void setWhite(Actor white) {
        this.white = white;
    }

    public Activity getInvitation() {
        return invitation;
    }

    public void setInvitation(Activity invitation) {
        this.invitation = invitation;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
}
