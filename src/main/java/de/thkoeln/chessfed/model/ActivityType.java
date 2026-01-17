package de.thkoeln.chessfed.model;

public enum ActivityType {
    INVITE,
    ACCEPT,
    CREATE,
    JOIN,
    PLAY,
    UNKNOWN;

    @Override
    public String toString() {
        switch (this) {
            case ACCEPT: return "Accept";
            case CREATE: return "Create";
            case INVITE: return "Invite";
            case JOIN: return "Join";
            case PLAY: return "chessfed:Play";
            default: return "Activity";
        }
    }

    public static ActivityType parse(String type) {
        switch (type) {
            case "Invite": return INVITE;
            case "Accept": return ACCEPT;
            case "Join": return JOIN;
            case "chessfed:Play": return PLAY;
            case "Create": return CREATE;
            default: return UNKNOWN;
        }
    }
}
