package de.thkoeln.chessfed.model;

public enum ChessPiece {
    PAWN,
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT;

    public static ChessPiece parse(String abbrev) {
        if (abbrev == null) return null;
        switch (abbrev) {
            case "p":
            case "P": return PAWN;
            case "k":
            case "K": return KING;
            case "q":
            case "Q": return QUEEN;
            case "r":
            case "R": return ROOK;
            case "b":
            case "B": return BISHOP;
            case "n":
            case "N": return KNIGHT;
            default: return null;
        }
    }

    public String getAbbrev() {
        switch (this) {
            case BISHOP:
                return "b";
            case KING:
                return "k";
            case KNIGHT:
                return "n";
            case PAWN:
                return "p";
            case QUEEN:
                return "q";
            case ROOK:
                return "r";
        }
        return null;
    }

    public String getAbbrev(ChessPlayer player) {
        return player.transformCase(getAbbrev());
    }
}
