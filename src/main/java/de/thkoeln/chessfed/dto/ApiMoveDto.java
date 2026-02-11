package de.thkoeln.chessfed.dto;

public class ApiMoveDto {
    
    private String source;
    private String target;
    private String promote;
    private Boolean capture;
    private Boolean castle;
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public Boolean getCapture() {
        return capture;
    }

    public void setCapture(Boolean capture) {
        this.capture = capture;
    }

    public Boolean getCastle() {
        return castle;
    }

    public void setCastle(Boolean castle) {
        this.castle = castle;
    }

}
