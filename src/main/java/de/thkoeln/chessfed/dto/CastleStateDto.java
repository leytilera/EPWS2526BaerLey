package de.thkoeln.chessfed.dto;

public class CastleStateDto {
    
    private boolean whiteShort;
    private boolean whiteLong;
    private boolean blackShort;
    private boolean blackLong;

    public boolean isWhiteShort() {
        return whiteShort;
    }

    public void setWhiteShort(boolean whiteShort) {
        this.whiteShort = whiteShort;
    }

    public boolean isWhiteLong() {
        return whiteLong;
    }

    public void setWhiteLong(boolean whiteLong) {
        this.whiteLong = whiteLong;
    }

    public boolean isBlackShort() {
        return blackShort;
    }

    public void setBlackShort(boolean blackShort) {
        this.blackShort = blackShort;
    }

    public boolean isBlackLong() {
        return blackLong;
    }

    public void setBlackLong(boolean blackLong) {
        this.blackLong = blackLong;
    }

}
