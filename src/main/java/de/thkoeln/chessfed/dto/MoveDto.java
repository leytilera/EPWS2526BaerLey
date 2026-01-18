package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveDto extends ActivityPubDto {
    private String published;
    private String source;
    private String target;
    private boolean capture;
    private boolean castle;
    private String promote;

    public MoveDto() {
        setType("chessfed:Move");
        withContext();
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    @JsonProperty("chessfed:source")
    public String getSource() {
        return source;
    }

    @JsonProperty("chessfed:source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("chessfed:target")
    public String getTarget() {
        return target;
    }

    @JsonProperty("chessfed:target")
    public void setTarget(String target) {
        this.target = target;
    }

    @JsonProperty("chessfed:capture")
    public boolean isCapture() {
        return capture;
    }

    @JsonProperty("chessfed:capture")
    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    @JsonProperty("chessfed:castle")
    public boolean isCastle() {
        return castle;
    }

    @JsonProperty("chessfed:castle")
    public void setCastle(boolean castle) {
        this.castle = castle;
    }

    @JsonProperty("chessfed:promote")
    public String getPromote() {
        return promote;
    }

    @JsonProperty("chessfed:promote")
    public void setPromote(String promote) {
        this.promote = promote;
    }

}
