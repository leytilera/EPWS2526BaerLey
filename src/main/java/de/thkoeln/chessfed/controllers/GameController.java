package de.thkoeln.chessfed.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.thkoeln.chessfed.dto.GameDto;
import de.thkoeln.chessfed.dto.MoveDto;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.services.IChessGameService;
import de.thkoeln.chessfed.services.IMappingService;

@RestController
public class GameController {

    private IChessGameService gameService;
    private IMappingService mappingService;
    
    @Autowired
    public GameController(IChessGameService gameService, IMappingService mappingService) {
        this.gameService = gameService;
        this.mappingService = mappingService;
    }

    @GetMapping(value = "/games/{id}", produces = "application/activity+json")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID id) {
        ChessGame chessGame = gameService.getGame(id);
        return ResponseEntity.ok(mappingService.map(chessGame, GameDto.class));
    }

    @GetMapping(value = "/games/{id}/moves/{moveCount}", produces = "application/activity+json")
    public ResponseEntity<MoveDto> getMove(@PathVariable UUID id, @PathVariable int moveCount) {
        ChessGame game = gameService.getGame(id);
        ChessMove move = gameService.getMove(game, moveCount);
        return ResponseEntity.ok(mappingService.map(move, MoveDto.class));
    }
    
}
