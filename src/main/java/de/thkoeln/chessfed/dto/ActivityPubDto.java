package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ActivityPubDto {
    
    private Object[] context;
    private String id;
    private String type;

    public ActivityPubDto() {

    }

    public ActivityPubDto(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public void withContext() {
        this.context = new Object[]{"https://www.w3.org/ns/activitystreams", new ContextDto()};
    }

    @JsonProperty("@context")
    @JsonInclude(Include.NON_NULL)
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

}
