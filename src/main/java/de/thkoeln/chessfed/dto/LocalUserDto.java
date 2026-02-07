package de.thkoeln.chessfed.dto;

import java.util.UUID;

public class LocalUserDto {

    private UUID id;
    private String username;
    private String actor;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
    
}
