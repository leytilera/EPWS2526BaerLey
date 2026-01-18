package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameDto extends ActivityPubDto {
    private String published;
    private String white;
    private String black;
    private boolean finished;
    private String winner;
    private String currentTurn;
    private String enPassantField;
    private String[][] board;
    private int totalItems;
    private ActivityPubDto[] items;

    public GameDto() {
        setType("chessfed:Game");
        withContext();
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    @JsonProperty("chessfed:white")
    public String getWhite() {
        return white;
    }

    @JsonProperty("chessfed:white")
    public void setWhite(String white) {
        this.white = white;
    }

    @JsonProperty("chessfed:black")
    public String getBlack() {
        return black;
    }

    @JsonProperty("chessfed:black")
    public void setBlack(String black) {
        this.black = black;
    }

    @JsonProperty("chessfed:finished")
    public boolean isFinished() {
        return finished;
    }

    @JsonProperty("chessfed:finished")
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @JsonProperty("chessfed:winner")
    public String getWinner() {
        return winner;
    }

    @JsonProperty("chessfed:winner")
    public void setWinner(String winner) {
        this.winner = winner;
    }

    @JsonProperty("chessfed:currentTurn")
    public String getCurrentTurn() {
        return currentTurn;
    }

    @JsonProperty("chessfed:currentTurn")
    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    @JsonProperty("chessfed:board")
    public String[][] getBoard() {
        return board;
    }

    @JsonProperty("chessfed:board")
    public void setBoard(String[][] board) {
        this.board = board;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public ActivityPubDto[] getItems() {
        return items;
    }

    public void setItems(ActivityPubDto[] items) {
        this.items = items;
    }

    @JsonProperty("chessfed:enPassantField")
    public String getEnPassantField() {
        return enPassantField;
    }

    @JsonProperty("chessfed:enPassantField")
    public void setEnPassantField(String enPassantField) {
        this.enPassantField = enPassantField;
    }
    
}
