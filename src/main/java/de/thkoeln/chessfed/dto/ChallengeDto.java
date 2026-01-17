package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChallengeDto extends ActivityPubDto {

    private String white;

    public ChallengeDto() {
        setType("chessfed:Challenge");
        withContext();
    }

    @JsonProperty("chessfed:white")
    @JsonInclude(Include.NON_NULL)
    public String getWhite() {
        return white;
    }

    @JsonProperty("chessfed:white")
    public void setWhite(String white) {
        this.white = white;
    }
    
}
