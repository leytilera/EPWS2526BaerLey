package de.thkoeln.chessfed.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ActivityDto;
import de.thkoeln.chessfed.dto.ActivityPubDto;
import de.thkoeln.chessfed.dto.ActorDto;
import de.thkoeln.chessfed.dto.CollectionDto;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IMappingService;
import de.thkoeln.chessfed.services.IActivityService;

@RestController
public class ActorController {

    private IActorService actorService;
    private IActivityService activityService;
    private IMappingService mappingService;

    @Autowired
    public ActorController(IActorService actorService, IActivityService activityService, IMappingService mappingService) {
        this.actorService = actorService;
        this.activityService = activityService;
        this.mappingService = mappingService;
    }
    
    @GetMapping(value = "/users/{id}", produces = "application/activity+json")
    public ResponseEntity<ActorDto> getActor(@PathVariable String id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(mappingService.map(actor, ActorDto.class));
    }

    @GetMapping(value = "/users/{id}/outbox", produces = "application/activity+json")
    public ResponseEntity<CollectionDto> getOutbox(@PathVariable String id) {
        Actor actor = actorService.getActorById(id);
        ActivityDto[] dtos = activityService.getOutbox(actor)
            .stream()
            .map((a) -> mappingService.map(a, ActivityDto.class))
            .toArray(ActivityDto[]::new);
        CollectionDto dto = new CollectionDto();
        dto.setId(actor.getOutbox());
        dto.setItems(dtos);
        dto.setTotalItems(dtos.length);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/users/{id}/inbox", consumes = {"application/json", "application/activity+json", "application/ld+json"})
    public HttpStatusCode postInbox(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Actor actor = actorService.getActorById(id);
        activityService.receiveActivity(actor, body);
        return HttpStatus.ACCEPTED;
    }

    @GetMapping(value = "/instance", produces = "application/activity+json")
    public ResponseEntity<ActorDto> getInstanceActor() {
        Actor actor = actorService.getInstanceActor();
        return ResponseEntity.ok(mappingService.map(actor, ActorDto.class));
    }

    @GetMapping(value = "/instance/outbox", produces = "application/activity+json")
    public ResponseEntity<CollectionDto> getInstanceOutbox() {
        CollectionDto dto = new CollectionDto();
        dto.setTotalItems(0);
        dto.setItems(new ActivityPubDto[0]);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/instance/inbox", consumes = {"application/json", "application/activity+json", "application/ld+json"})
    public HttpStatusCode postInstanceInbox(@RequestBody Map<String, Object> body) {
        Actor actor = actorService.getInstanceActor();
        activityService.receiveActivity(actor, body);
        return HttpStatus.ACCEPTED;
    }

}
