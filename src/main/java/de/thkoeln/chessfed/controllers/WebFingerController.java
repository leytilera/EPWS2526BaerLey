package de.thkoeln.chessfed.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        Actor actor = actorService.getActorByAcct(resource);
        JsonResourceDescriptorDto jrd = new JsonResourceDescriptorDto();
        jrd.setSubject(resource);
        LinkDto link = new LinkDto(actor.getUrl());
        jrd.getLinks().add(link);
        return ResponseEntity.ok(jrd);
    }

}
