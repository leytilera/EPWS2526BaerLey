package de.thkoeln.chessfed.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thkoeln.chessfed.dto.JsonResourceDescriptorDto;
import de.thkoeln.chessfed.dto.LinkDto;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.services.IActorService;

@RestController
public class WebFingerController {
    
    private IActorService actorService;

    @Autowired
    public WebFingerController(IActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping(value = "/.well-known/webfinger", produces = {"application/jrd+json", "application/json"})
    public ResponseEntity<JsonResourceDescriptorDto> webfinger(@RequestParam String resource) {
        if (!resource.startsWith("acct:") || resource.split("@").length != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Actor actor = actorService.getActorByAcct(resource);
        if (actor == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        JsonResourceDescriptorDto jrd = new JsonResourceDescriptorDto();
        jrd.setSubject(resource);
        LinkDto link = new LinkDto(actor.getUrl());
        jrd.getLinks().add(link);
        return ResponseEntity.ok(jrd);
    }

}
