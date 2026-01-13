package de.thkoeln.chessfed.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ChessMove {
    
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne
    private ChessGame game;
    private int moveCount;
    @Enumerated(EnumType.ORDINAL)
    private ChessPlayer player;
    private int sourceField;
    private int targetField;
    private boolean castle;
    private boolean capture;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public int getMoveCount() {
        return moveCount;
    }
    
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
    
    public ChessPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ChessPlayer player) {
        this.player = player;
    }

    public int getSourceField() {
        return sourceField;
    }

    public void setSourceField(int sourceField) {
        this.sourceField = sourceField;
    }

    public int getTargetField() {
        return targetField;
    }

    public void setTargetField(int targetField) {
        this.targetField = targetField;
    }

    public boolean isCastle() {
        return castle;
    }

    public void setCastle(boolean castle) {
        this.castle = castle;
    }

    public boolean isCapture() {
        return capture;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

}
