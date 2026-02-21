package de.thkoeln.chessfed.dto;

public class ProfileGameViewModel {
    private String gameId;
    private String whiteUsername;
    private String blackUsername;
    private String whiteHandle;
    private String blackHandle;
    private String whiteScore;
    private String blackScore;
    private String moveCount;
    private String status;

    public void setGameId(String date) {
        this.gameId = date;
    }

    public String getGameId() {
        return gameId;
    }

    public void setWhiteUsername(String whiteName) {
        this.whiteUsername = whiteName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setBlackUsername(String blackName) {
        this.blackUsername = blackName;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setWhiteHandle(String whiteInstance) {
        this.whiteHandle = whiteInstance;
    }

    public String getWhiteHandle() {
        return whiteHandle;
    }

    public void setBlackHandle(String blackInstance) {
        this.blackHandle = blackInstance;
    }

    public String getBlackHandle() {
        return blackHandle;
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

    public void setMoveCount(String moveCount) {
        this.moveCount = moveCount;
    }

    public String getMoveCount() {
        return moveCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
