package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameDto {
    private Object[] context;
    private String id;
    private String type;
    private String published;
    private String white;
    private String black;
    private boolean finished;
    private String winner;
    private String currentTurn;
    private String[][] board;
    private int totalItems;
    private ReferenceDto[] items;

    @JsonProperty("@context")
    public Object[] getContext() {
        return context;
    }

    @JsonProperty("@context")
    public void setContext(Object[] context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public ReferenceDto[] getItems() {
        return items;
    }

    public void setItems(ReferenceDto[] items) {
        this.items = items;
    }
    
}
