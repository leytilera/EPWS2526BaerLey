package de.thkoeln.chessfed.controllers;

import java.util.HashMap;
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

import de.thkoeln.chessfed.dto.ActorDto;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IActivityService;

@RestController
public class ActorController {

    private IActorService actorService;
    private IActivityService activityService;

    @Autowired
    public ActorController(IActorService actorService, IActivityService activityService) {
        this.actorService = actorService;
        this.activityService = activityService;
    }
    
    @GetMapping(value = "/users/{id}", produces = "application/activity+json")
    public ResponseEntity<ActorDto> getActor(@PathVariable String id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(new ActorDto(actor.getId(), "Person", id, id, actor.getInbox(), actor.getOutbox()));
    }

    @GetMapping(value = "/users/{id}/outbox", produces = "application/activity+json")
    public ResponseEntity<Map<String, Object>> getOutbox(@PathVariable String id) {
        Map<String, Object> res = new HashMap<>();
        res.put("@context", "https://www.w3.org/ns/activitystreams");
        res.put("type", "OrderedCollection");
        res.put("totalItems", 0);
        res.put("orderedItems", new Object[0]);
        return ResponseEntity.ok(res);
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
        return ResponseEntity.ok(new ActorDto(actor.getId(), "Person", actor.getLocalpart(), actor.getLocalpart(), actor.getInbox(), actor.getOutbox()));
    }

    @GetMapping(value = "/instance/outbox", produces = "application/activity+json")
    public ResponseEntity<Map<String, Object>> getInstanceOutbox() {
        Map<String, Object> res = new HashMap<>();
        res.put("@context", "https://www.w3.org/ns/activitystreams");
        res.put("type", "OrderedCollection");
        res.put("totalItems", 0);
        res.put("orderedItems", new Object[0]);
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/instance/inbox", consumes = {"application/json", "application/activity+json", "application/ld+json"})
    public HttpStatusCode postInstanceInbox(@RequestBody Map<String, Object> body) {
        Actor actor = actorService.getInstanceActor();
        activityService.receiveActivity(actor, body);
        return HttpStatus.ACCEPTED;
    }

}
