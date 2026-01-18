package de.thkoeln.chessfed.model;

public enum ChessPlayer {
    NONE,
    WHITE,
    BLACK;

    public ChessPlayer getOpponent() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                return NONE;
        }
    }

    public static ChessPlayer parse(String abbrev) {
        if (abbrev == null || abbrev.isEmpty()) return NONE;
        return Character.isUpperCase(abbrev.charAt(0)) ? BLACK : WHITE;
    }
}
