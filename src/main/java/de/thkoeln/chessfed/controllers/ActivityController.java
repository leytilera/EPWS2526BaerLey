package de.thkoeln.chessfed.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.IActivityRepository;

@RestController
public class ActivityController {
 
    private IActivityRepository activityRepository;

    public ActivityController(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<ActivityDto> getActivity(@PathVariable UUID id) {
        Activity activity = activityRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getFederation().getId());
        dto.setType(activity.getType().toString());
        if (activity.getActor() != null) {
            dto.setActor(new ActivityPubDto(activity.getActor().getId(), "Person"));
        }
        if (activity.getObject() != null) {
            dto.setObject(new ActivityPubDto(activity.getObject().getId(), activity.getObject().getType().toString()));
        }
        if (activity.getTarget() != null) {
            ActivityPubDto[] target = new ActivityPubDto[activity.getTarget().length];
            for (int i = 0; i < target.length; i++) {
                target[i] = new ActivityPubDto(activity.getTarget()[i].getId(), activity.getTarget()[i].getType().toString());
            }
            dto.setTarget(target);
        }
        return ResponseEntity.ok(dto);
    }

}
