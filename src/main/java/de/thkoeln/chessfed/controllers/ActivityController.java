package de.thkoeln.chessfed.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Activity;
import de.thkoeln.chessfed.model.IActivityRepository;
import de.thkoeln.chessfed.services.IMappingService;

@RestController
public class ActivityController {
 
    private IActivityRepository activityRepository;
    private IMappingService mappingService;

    public ActivityController(IActivityRepository activityRepository, IMappingService mappingService) {
        this.activityRepository = activityRepository;
        this.mappingService = mappingService;
    }

    @GetMapping("/activities/{id}")
    public ResponseEntity<ActivityDto> getActivity(@PathVariable UUID id) {
        Activity activity = activityRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        ActivityDto dto = mappingService.map(activity, ActivityDto.class);
        return ResponseEntity.ok(dto);
    }

}
