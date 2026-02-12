package de.thkoeln.chessfed.controllers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.ApiChallengeDto;
import de.thkoeln.chessfed.dto.ApiGameDto;
import de.thkoeln.chessfed.dto.ApiInviteDto;
import de.thkoeln.chessfed.dto.ApiMoveDto;
import de.thkoeln.chessfed.dto.ApiUserDto;
import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.Challenge;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.IChallengeRepository;
import de.thkoeln.chessfed.model.LocalUser;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IChessGameService;
import de.thkoeln.chessfed.services.IMappingService;
import de.thkoeln.chessfed.services.IUserInteractionService;

@RestController
public class ClientController {
    
    private IUserInteractionService interactionService;
    private IChessGameService gameService;
    private IChallengeRepository challengeRepository;
    private IActorService actorService;
    private IMappingService mappingService;
    
    @Autowired
    public ClientController(IUserInteractionService interactionService, IChessGameService gameService, IChallengeRepository challengeRepository, IActorService actorService, IMappingService mappingService) {
        this.interactionService = interactionService;
        this.gameService = gameService;
        this.challengeRepository = challengeRepository;
        this.actorService = actorService;
        this.mappingService = mappingService;
    }

    @GetMapping("/api/user")
    public ResponseEntity<ApiUserDto> getUser(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        LocalUser usr = interactionService.getUser(principal.getName());
        ApiUserDto dto = new ApiUserDto();
        dto.setId(usr.getId());
        dto.setUsername(usr.getUsername());
        dto.setActor(usr.getActor().getFederation().getId());
        dto.setUsername(usr.getActor().getLocalpart() + "@" + usr.getActor().getDomain());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<ApiUserDto> getUser(@PathVariable String id) {
        Optional<Actor> user = Optional.empty();
        if (id.contains("@")) {
            user = Optional.of(actorService.getActorByAcct("acct:" + id));
        } else {
            user = Optional.of(actorService.getActorById(id));
        }
        return user
            .map((u) -> {
                ApiUserDto dto = new ApiUserDto();
                dto.setId(null);
                dto.setUsername(u.getLocalpart());
                dto.setActor(u.getFederation().getId());
                dto.setHandle(u.getLocalpart() + "@" + u.getDomain());
                return dto;
            })
            .map(ResponseEntity::ok)
            .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/api/users/{id}/games")
    public ResponseEntity<ApiGameDto[]> getUserGames(@PathVariable String id) {
        Optional<Actor> user = Optional.empty();
        if (id.contains("@")) {
            user = Optional.of(actorService.getActorByAcct("acct:" + id));
        } else {
            user = Optional.of(actorService.getActorById(id));
        }
        return user.map(gameService::getGames)
            .map(List::stream)
            .map((s) -> s.map((g) -> mappingService.map(g, ApiGameDto.class)))
            .map((s) -> s.toArray(ApiGameDto[]::new))
            .map(ResponseEntity::ok)
            .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/api/games")
    public ResponseEntity<ApiGameDto[]> getCurrentGames(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        LocalUser usr = interactionService.getUser(principal.getName());
        ApiGameDto[] games = gameService.getGames(usr.getActor()).stream()
            .filter((g) -> !g.isHasEnded())
            .map((g) -> mappingService.map(g, ApiGameDto.class))
            .map((dto) -> {dto.setYourTurn(usr.getActor().getId().equals(dto.getCurrentTurn())); return dto;})
            .toArray(ApiGameDto[]::new);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/api/games/{id}")
    public ResponseEntity<ApiGameDto> getGame(@AuthenticationPrincipal AuthenticatedPrincipal principal, @PathVariable UUID id) {
        LocalUser usr = interactionService.getUser(principal.getName());
        ChessGame game = gameService.getGame(id);
        ApiGameDto dto = mappingService.map(game, ApiGameDto.class);
        dto.setYourTurn(usr.getActor().getId().equals(dto.getCurrentTurn()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/games/{id}/moves")
    public ResponseEntity<ApiMoveDto[]> getMoves(@PathVariable UUID id) {
        ChessGame game = gameService.getGame(id);
        ApiMoveDto[] dto = gameService.getMoves(game)
            .stream()
            .sorted(Comparator.comparingInt(ChessMove::getMoveCount))
            .map((m) -> mappingService.map(m, ApiMoveDto.class))
            .toArray(ApiMoveDto[]::new);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/games/{id}/moves")
    public void playMove(@AuthenticationPrincipal AuthenticatedPrincipal principal, @PathVariable UUID id, @RequestBody ApiMoveDto move) {
        LocalUser usr = interactionService.getUser(principal.getName());
        interactionService.playMove(usr, id, move.getSource(), move.getTarget(), move.getPromote());
    }

    @GetMapping("/api/challenges")
    public ResponseEntity<ApiChallengeDto[]> getCurrentChallenges(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        LocalUser usr = interactionService.getUser(principal.getName());
        ApiChallengeDto[] challenges = Arrays.stream(interactionService.getOpenChallenges(usr))
            .map((c) -> mappingService.map(c, ApiChallengeDto.class))
            .toArray(ApiChallengeDto[]::new);
        return ResponseEntity.ok(challenges);
    }

    @PostMapping("/api/challenges")
    public void createChallenge(@AuthenticationPrincipal AuthenticatedPrincipal principal, @RequestBody ApiInviteDto invitation) {
        LocalUser usr = interactionService.getUser(principal.getName());
        interactionService.createInvitation(usr, invitation.getOpponent());
    }

    @GetMapping("/api/challenges/{id}")
    public ResponseEntity<ApiChallengeDto> getChallenge(@AuthenticationPrincipal AuthenticatedPrincipal principal, @PathVariable UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        ApiChallengeDto dto = mappingService.map(challenge, ApiChallengeDto.class);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/challenges/{id}")
    public void acceptChallenge(@AuthenticationPrincipal AuthenticatedPrincipal principal, @PathVariable UUID id, @RequestBody Boolean accept) {
        LocalUser usr = interactionService.getUser(principal.getName());
        if (accept) {
            interactionService.acceptInvitation(usr, id);
        }
    }

}
