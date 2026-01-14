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
}
