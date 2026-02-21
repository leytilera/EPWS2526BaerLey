package de.thkoeln.chessfed.dto;

import java.util.UUID;

public class ProfileViewModel {
    private boolean ownProfile;
    private UUID id; 
    private String username;
    private String handle;
    private String bio;
    private String countFinishedGames;

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

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setOwnProfile(boolean ownProfile) {
        this.ownProfile = ownProfile;
    }

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public void setCountFinishedGames(String countFinishedGames) {
        this.countFinishedGames = countFinishedGames;
    }

    public String getCountFinishedGames() {
        return countFinishedGames;
    }
}
