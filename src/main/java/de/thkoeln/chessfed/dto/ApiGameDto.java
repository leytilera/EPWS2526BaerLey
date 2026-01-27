package de.thkoeln.chessfed.dto;

import java.util.UUID;

public class ApiGameDto {

    private UUID id;
    private String currentTurn; //TODO: this is only for testing, will be replaced

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
    
}
