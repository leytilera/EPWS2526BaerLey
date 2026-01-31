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

    public String transformCase(String abbrev) {
        if (abbrev == null) return null;
        switch (this) {
            case WHITE:
                return abbrev.toUpperCase();
            case BLACK:
                return abbrev.toLowerCase();
            default:
                return null;
        }
    }

    public <T> T get(T white, T black) {
        switch (this) {
            case WHITE:
                return white;
            case BLACK:
                return black;
            default:
                return null;
        }
    }

    public boolean isPlayer() {
        return this == WHITE || this == BLACK;
    }

}
