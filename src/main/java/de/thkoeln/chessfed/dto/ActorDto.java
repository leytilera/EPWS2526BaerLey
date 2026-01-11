package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorDto {
    
    private String context;
    private String id;
    private String type;
    private String preferredUsername;
    private String name;
    private String inbox;
    private String outbox;

    public ActorDto(String id, String type, String preferredUsername, String name, String inbox, String outbox) {
        this.context = "https://www.w3.org/ns/activitystreams";
        this.id = id;
        this.type = type;
        this.preferredUsername = preferredUsername;
        this.name = name;
        this.inbox = inbox;
        this.outbox = outbox;
    }

    @JsonProperty("@context")
    public String getContext() {
        return context;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public String getName() {
        return name;
    }

    public String getInbox() {
        return inbox;
    }

    public String getOutbox() {
        return outbox;
    }

}
