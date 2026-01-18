package de.thkoeln.chessfed.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IUserInteractionService;

@RestController
public class DebugController {
    
    private IUserInteractionService userInteractionService;
    private ILocalUserRepository userRepository;
    private IActorService actorService;

    @Autowired
    public DebugController(IUserInteractionService userInteractionService, ILocalUserRepository userRepository, IActorService actorService) {
        this.userInteractionService = userInteractionService;
        this.userRepository = userRepository;
        this.actorService = actorService;
    }

    @PostMapping("/debug/games/{gameId}")
    public void playMove(@PathVariable UUID gameId, String username, String source, String target) {
        LocalUser user = userRepository.getByUsername(username).orElseThrow(ResourceNotFoundException::new);
        userInteractionService.playMove(user, gameId, source, target, null);
    } 

    @PostMapping("/debug/invite")
    public void invite(String username, String opponent) { 
        LocalUser user = userRepository.getByUsername(username).orElseThrow(ResourceNotFoundException::new);
        userInteractionService.createInvitation(user, opponent);
    }

    @PostMapping("/debug/challenges/{challengeId}")
    public void accept(@PathVariable UUID challengeId, String username) {
        LocalUser user = userRepository.getByUsername(username).orElseThrow(ResourceNotFoundException::new);
        userInteractionService.acceptInvitation(user, challengeId);
    }

    @PostMapping("/debug/users")
    public void createUser(String username) {
        actorService.createUser(username);
    }

}
