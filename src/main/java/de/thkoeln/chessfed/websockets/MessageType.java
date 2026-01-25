package de.thkoeln.chessfed.websockets;

public enum MessageType {
    CREATE_GAME,
    CHALLENGE_INVITE,
    CHALLENGE_ACCEPT,
    MOVE;

    public static MessageType parse(int ordinal) {
        if (ordinal < 0 || ordinal >= MessageType.values().length) {
            return null;
        }
        return MessageType.values()[ordinal];
    }
}
