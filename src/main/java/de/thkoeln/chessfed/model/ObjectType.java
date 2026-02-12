package de.thkoeln.chessfed.model;

public enum ObjectType {
    ACTIVITY,
    ACTOR,
    GAME,
    MOVE,
    CHALLENGE,
    INVITE,
    ACCEPT,
    CREATE,
    JOIN,
    PLAY,
    PERSON,
    SERVICE,
    APPLICATION,
    UNKNOWN;

    @Override
    public String toString() {
        switch (this) {
            case ACTIVITY: return "Activity";
            case ACTOR: return "Actor";
            case GAME: return "chessfed:Game";
            case MOVE: return "chessfed:Move";
            case CHALLENGE: return "chessfed:Challenge";
            case ACCEPT: return "Accept";
            case CREATE: return "Create";
            case INVITE: return "Invite";
            case JOIN: return "Join";
            case PLAY: return "chessfed:Play";
            case PERSON: return "Person";
            case SERVICE: return "Service";
            case APPLICATION: return "Application";
            default: return "Object";
        }
    }

    public static ObjectType parse(String type) {
        switch (type) {
            case "chessfed:Game": return GAME;
            case "chessfed:Move": return MOVE;
            case "chessfed:Challenge": return CHALLENGE;
            case "Actor": return ACTOR;
            case "Person": return PERSON;
            case "Service": return SERVICE;
            case "Application": return APPLICATION;
            case "Activity": return ACTIVITY;
            case "Invite": return INVITE;
            case "Accept": return ACCEPT;
            case "Join": return JOIN;
            case "chessfed:Play": return PLAY;
            case "Create": return CREATE;
            default: return UNKNOWN;
        }
    }

    public ObjectType simplify() {
        switch (this) {
            case ACCEPT: 
            case CREATE:
            case INVITE: 
            case JOIN:
            case PLAY: return ACTIVITY;
            case PERSON: 
            case SERVICE: 
            case APPLICATION: return ACTOR;
            default: return this;
        }
    }

}
