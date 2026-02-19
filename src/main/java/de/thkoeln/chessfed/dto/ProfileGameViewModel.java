package de.thkoeln.chessfed.dto;

public class ProfileGameViewModel {
    private String date;
    private String whiteName;
    private String blackName;
    private String whiteInstance;
    private String blackInstance;
    private String whiteScore;
    private String blackScore;
    private String gameUrl;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setWhiteName(String whiteName) {
        this.whiteName = whiteName;
    }

    public String getWhiteName() {
        return whiteName;
    }

    public void setBlackName(String blackName) {
        this.blackName = blackName;
    }

    public String getBlackName() {
        return blackName;
    }

    public void setWhiteInstance(String whiteInstance) {
        this.whiteInstance = whiteInstance;
    }

    public String getWhiteInstance() {
        return whiteInstance;
    }

    public void setBlackInstance(String blackInstance) {
        this.blackInstance = blackInstance;
    }

    public String getBlackInstance() {
        return blackInstance;
    }

    public void setWhiteScore(String whiteScore) {
        this.whiteScore = whiteScore;
    }

    public String getWhiteScore() {
        return whiteScore;
    }

    public void setBlackScore(String blackScore) {
        this.blackScore = blackScore;
    }

    public String getBlackScore() {
        return blackScore;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public String getGameUrl() {
        return gameUrl;
    }
}
