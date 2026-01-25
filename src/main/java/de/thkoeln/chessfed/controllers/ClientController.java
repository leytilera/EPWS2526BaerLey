package de.thkoeln.chessfed.controllers;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ApiChallengeDto;
import de.thkoeln.chessfed.dto.ApiGameDto;
import de.thkoeln.chessfed.dto.ApiMoveDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.IChallengeRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IChessGameService;
import de.thkoeln.chessfed.services.IUserInteractionService;

@RestController
public class ClientController {
    
    private IUserInteractionService interactionService;
    private IChessGameService gameService;
    private IChallengeRepository challengeRepository;
    
    @Autowired
    public ClientController(IUserInteractionService interactionService, IChessGameService gameService) {
        this.interactionService = interactionService;
        this.gameService = gameService;
    }

    @GetMapping("/api/games")
    public ResponseEntity<ApiGameDto[]> getCurrentGames(@RequestParam String user) {
        LocalUser usr = interactionService.getUser(user);
        ApiGameDto[] games = gameService.getGames(usr.getActor()).stream()
            .filter((g) -> !g.isHasEnded())
            .map(this::mapToDto)
            .toArray(ApiGameDto[]::new);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/api/games/{id}")
    public ResponseEntity<ApiGameDto> getGame(@RequestParam String user, @PathVariable UUID id) {
        ChessGame game = gameService.getGame(id);
        ApiGameDto dto = mapToDto(game);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/games/{id}/moves")
    public void playMove(@RequestParam String user, @PathVariable UUID id, @RequestBody ApiMoveDto move) {
        LocalUser usr = interactionService.getUser(user);
        interactionService.playMove(usr, id, move.getSource(), move.getTarget(), move.getPromote());
    }

    @GetMapping("/api/challenges")
    public ResponseEntity<ApiChallengeDto[]> getCurrentChallenges(@RequestParam String user) {
        LocalUser usr = interactionService.getUser(user);
        ApiChallengeDto[] challenges = Arrays.stream(interactionService.getOpenChallenges(usr))
            .map(this::mapToDto)
            .toArray(ApiChallengeDto[]::new);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/api/challenges/{id}")
    public ResponseEntity<ApiChallengeDto> getChallenge(@RequestParam String user, @PathVariable UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        ApiChallengeDto dto = mapToDto(challenge);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/challenges/{id}")
    public void acceptChallenge(@RequestParam String user, @PathVariable UUID id, @RequestBody Boolean accept) {
        LocalUser usr = interactionService.getUser(user);
        if (accept) {
            interactionService.acceptInvitation(usr, id);
        }
    }

    private ApiGameDto mapToDto(ChessGame game) {
        ApiGameDto dto = new ApiGameDto();
        dto.setId(game.getId());
        return dto;
    }

    private ApiChallengeDto mapToDto(Challenge challenge) {
        ApiChallengeDto dto = new ApiChallengeDto();
        dto.setId(challenge.getId());
        return dto;
    }

}
