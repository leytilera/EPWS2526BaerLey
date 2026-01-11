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

@RestController
public class ActorController {

    private IActorService actorService;

    @Autowired
    public ActorController(IActorService actorService) {
        this.actorService = actorService;
    }
    
    @GetMapping(value = "/users/{id}", produces = "application/activity+json")
    public ResponseEntity<ActorDto> getActor(@PathVariable String id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(new ActorDto(actor.getUrl(), "Person", id, id, actor.getUrl() + "/inbox", actor.getUrl() + "/outbox"));
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
        return HttpStatus.ACCEPTED;
    }

}
