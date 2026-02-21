package de.thkoeln.chessfed.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Actor {
 
    @Id
    private String id;
    private String localpart;
    private String domain;
    private String inbox;
    private String outbox;
    @OneToOne
    private FederatedObject federation;

    public String getLocalpart() {
        return localpart;
    }

    public void setLocalpart(String localpart) {
        this.localpart = localpart;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInbox() {
        return inbox;
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }

    public String getOutbox() {
        return outbox;
    }

    public void setOutbox(String outbox) {
        this.outbox = outbox;
    }

    public FederatedObject getFederation() {
        return federation;
    }

    public void setFederation(FederatedObject federation) {
        this.federation = federation;
    }

}
