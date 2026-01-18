package de.thkoeln.chessfed.model;

public enum ObjectType {
    ACTIVITY,
    ACTOR,
    GAME,
    MOVE,
    CHALLENGE;

    @Override
    public String toString() {
        switch (this) {
            case ACTIVITY: return "Activity";
            case ACTOR: return "Actor";
            case GAME: return "chessfed:Game";
            case MOVE: return "chessfed:Move";
            case CHALLENGE: return "chessfed:Challenge";
            default: return "Object";
        }
    }

    public static ObjectType parse(String type) {
        switch (type) {
            case "chessfed:Game": return GAME;
            case "chessfed:Move": return MOVE;
            case "chessfed:Challenge": return CHALLENGE;
            case "Actor":
            case "Person": 
            case "Service":
            case "Application": return ACTOR;
            case "Activity":
            case "Invite": 
            case "Accept": 
            case "Join": 
            case "chessfed:Play": 
            case "Create": return ACTIVITY;
            default: return null;
        }
    }

}
