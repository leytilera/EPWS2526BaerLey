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

@RestController
public class ChallengeController {
    
    private IChallengeRepository challengeRepository;

    @Autowired
    public ChallengeController(IChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/challenges/{id}")
    public ResponseEntity<ChallengeDto> getChallenge(@PathVariable UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        ChallengeDto dto = new ChallengeDto();
        dto.setId(challenge.getFederation().getId());
        if (challenge.getWhite() != null) {
            dto.setWhite(challenge.getWhite().getFederation().getId());
        }
        return ResponseEntity.ok(dto);
    }

}
