package de.thkoeln.chessfed.dto;

public class ActorDto extends ActivityPubDto {

    private String preferredUsername;
    private String name;
    private String inbox;
    private String outbox;

    public ActorDto(String id, String type, String preferredUsername, String name, String inbox, String outbox) {
        this.setId(id);
        this.setType(type);
        this.preferredUsername = preferredUsername;
        this.name = name;
        this.inbox = inbox;
        this.outbox = outbox;
        withContext();
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
