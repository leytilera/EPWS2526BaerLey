package de.thkoeln.chessfed.services;

import java.util.List;
import java.util.Map;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.exception.InvalidActivityException;

public class MappingService {

    @SuppressWarnings({"unchecked", "ALEC"})
    public ActivityDto parseActivity(Map<String, Object> json) {
        ActivityDto dto = new ActivityDto();
        if (json.get("id") instanceof String) {
            dto.setId((String) json.get("id"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("type") instanceof String) {
            dto.setType((String) json.get("type"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("actor") instanceof Map) {
            Map<String, Object> actor = (Map<String, Object>) json.get("actor");
            dto.setActor(new ActivityPubDto((String) actor.get("id"), (String) actor.get("type")));
        } else if (json.get("actor") instanceof String) {
            dto.setActor(new ActivityPubDto((String) json.get("actor"), "Actor"));
        } else {
            throw new InvalidActivityException();
        }
        if (json.get("object") instanceof Map) {
            Map<String, Object> object = (Map<String, Object>) json.get("object");
            dto.setObject(new ActivityPubDto((String) object.get("id"), (String) object.get("type")));
        } else if (json.get("object") instanceof String) {
            dto.setObject(new ActivityPubDto((String) json.get("object"), "Object"));
        }
        if (json.get("target") instanceof Map) {
            Map<String, Object> target = (Map<String, Object>) json.get("target");
            dto.setTarget(new ActivityPubDto[]{new ActivityPubDto((String) target.get("id"), (String) target.get("type"))});
        } else if (json.get("target") instanceof String) {
            dto.setTarget(new ActivityPubDto[]{new ActivityPubDto((String) json.get("target"), "Object")});
        } else if (json.get("target") instanceof List) {
            dto.setTarget(((List<Object>) json.get("target")).stream()
                .filter((e) -> e instanceof Map)
                .map((e) -> (Map<String, Object>) e)
                .filter((e) -> e.get("id") instanceof String && e.get("type") instanceof String)
                .map((e) -> new ActivityPubDto((String) e.get("id"), (String) e.get("type")))
                .toArray(ActivityPubDto[]::new)
            );
        }
        return dto;
    }

}
