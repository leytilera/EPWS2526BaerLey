package de.thkoeln.chessfed.dto;

import java.util.UUID;

public class ApiGameDto {

    private UUID id;
    private String currentTurn; 
    private CastleStateDto castleState;
    private String[][] board;
    private String enPassantField;
    private String white;
    private String black;
    private boolean yourTurn;
    private boolean finished;
    private String winner;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public CastleStateDto getCastleState() {
        return castleState;
    }

    public void setCastleState(CastleStateDto castleState) {
        this.castleState = castleState;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public String getEnPassantField() {
        return enPassantField;
    }

    public void setEnPassantField(String enPassantField) {
        this.enPassantField = enPassantField;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
