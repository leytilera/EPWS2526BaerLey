package de.thkoeln.chessfed.dto;

public class LinkDto {
    
    private String rel;
    private String type;
    private String href;

    public LinkDto() {

    }
    
    public LinkDto(String href) {
        this.rel = "self";
        this.type = "application/activity+json";
        this.href = href;
    }

    public LinkDto(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
