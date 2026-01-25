package de.thkoeln.chessfed.controllers;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/debug/users/{username}/games")
    public ResponseEntity<UUID[]> getGames(@PathVariable String username) {
        LocalUser user = userRepository.getByUsername(username).orElseThrow(ResourceNotFoundException::new);
        UUID[] games = userInteractionService.getGames(user);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/debug/users/{username}/challenges")
    public ResponseEntity<UUID[]> getChallenges(@PathVariable String username) {
        LocalUser user = userRepository.getByUsername(username).orElseThrow(ResourceNotFoundException::new);
        UUID[] games = Arrays.stream(userInteractionService.getOpenChallenges(user)).map((c) -> c.getId()).toArray(UUID[]::new);
        return ResponseEntity.ok(games);
    }

}
