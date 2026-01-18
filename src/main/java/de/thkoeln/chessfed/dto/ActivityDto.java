package de.thkoeln.chessfed.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ActivityDto extends ActivityPubDto {
    private ActivityPubDto actor;
    private ActivityPubDto object;
    private ActivityPubDto[] target;

    public ActivityDto() {
        withContext();
    }

    @JsonInclude(Include.NON_NULL)
    public ActivityPubDto getActor() {
        return actor;
    }

    public void setActor(ActivityPubDto actor) {
        this.actor = actor;
    }

    @JsonInclude(Include.NON_NULL)
    public ActivityPubDto getObject() {
        return object;
    }

    public void setObject(ActivityPubDto object) {
        this.object = object;
    }

    @JsonInclude(Include.NON_NULL)
    public ActivityPubDto[] getTarget() {
        return target;
    }

    @JsonFormat(with = Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public void setTarget(ActivityPubDto[] target) {
        this.target = target;
    }
    
}
