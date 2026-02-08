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

    public static ChessPiece fromField(byte fieldFlag) throws IllegalArgumentException {
        if (fieldFlag == 0) return null;
        int flag = fieldFlag & 0xFF;
        boolean isWhite = (fieldFlag & 1) == 0;
        int pieceId = isWhite ? (flag >>> 1) - 1 : (flag - 1) >>> 1;
        if (pieceId < 0 || pieceId >= ChessPiece.values().length) throw new IllegalArgumentException("Invalid field flag");
        return ChessPiece.values()[pieceId];
    }

    public byte toField(ChessPlayer player) {
        if (player == ChessPlayer.NONE) return 0;
        int flag = player == ChessPlayer.WHITE ? (this.ordinal() + 1) << 1 : (this.ordinal() << 1) + 1;
        return (byte) (flag & 0xFF);
    }
}
