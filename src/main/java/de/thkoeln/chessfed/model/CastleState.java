package de.thkoeln.chessfed.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class CastleState {
    
    private boolean whiteShort;
    private boolean whiteLong;
    private boolean blackShort;
    private boolean blackLong;

    public CastleState(boolean whiteShort, boolean whiteLong, boolean blackShort, boolean blackLong) {
        this.whiteShort = whiteShort;
        this.whiteLong = whiteLong;
        this.blackShort = blackShort;
        this.blackLong = blackLong;
    }

    public CastleState() {
        
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (whiteShort ? 1231 : 1237);
        result = prime * result + (whiteLong ? 1231 : 1237);
        result = prime * result + (blackShort ? 1231 : 1237);
        result = prime * result + (blackLong ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CastleState other = (CastleState) obj;
        if (whiteShort != other.whiteShort)
            return false;
        if (whiteLong != other.whiteLong)
            return false;
        if (blackShort != other.blackShort)
            return false;
        if (blackLong != other.blackLong)
            return false;
        return true;
    }

}
