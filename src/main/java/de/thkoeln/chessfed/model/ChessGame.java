package de.thkoeln.chessfed.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ChessGame {
    
    @Id
    private UUID id = UUID.randomUUID();
    @Enumerated(EnumType.ORDINAL)
    private ChessPlayer currentTurn;
    private int enPassentField = -1;
    private byte castleState;
    private byte[] fields = new byte[64];
    @ManyToOne
    private Actor whitePlayer;
    @ManyToOne
    private Actor blackPlayer;
    private boolean hasEnded;
    private int moveCounter;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ChessPlayer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(ChessPlayer currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getEnPassentField() {
        return enPassentField;
    }

    public void setEnPassentField(int enPassentField) {
        this.enPassentField = enPassentField;
    }

    public byte getCastleState() {
        return castleState;
    }

    public void setCastleState(byte castleState) {
        this.castleState = castleState;
    }

    public byte[] getFields() {
        return fields;
    }

    public void setFields(byte[] fields) {
        this.fields = fields;
    }

    public Actor getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Actor whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Actor getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Actor blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public boolean isHasEnded() {
        return hasEnded;
    }

    public void setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

}
