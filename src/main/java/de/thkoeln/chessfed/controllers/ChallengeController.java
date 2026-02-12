package de.thkoeln.chessfed.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ChallengeDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.IChallengeRepository;
import de.thkoeln.chessfed.services.IMappingService;

@RestController
public class ChallengeController {
    
    private IChallengeRepository challengeRepository;
    private IMappingService mappingService;

    @Autowired
    public ChallengeController(IChallengeRepository challengeRepository, IMappingService mappingService) {
        this.challengeRepository = challengeRepository;
        this.mappingService = mappingService;
    }

    @GetMapping("/challenges/{id}")
    public ResponseEntity<ChallengeDto> getChallenge(@PathVariable UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(mappingService.map(challenge, ChallengeDto.class));
    }

}
