package de.thkoeln.chessfed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.services.IActorService;
import de.thkoeln.chessfed.services.IChessGameService;

@Component
public class TestData implements CommandLineRunner {

    private IActorService actorService;
    private IChessGameService gameService;

    @Autowired
    public TestData(IActorService actorService, IChessGameService gameService) {
        this.actorService = actorService;
        this.gameService = gameService;
    }

    @Override
    @Profile("!prod")
    public void run(String... args) throws Exception {
        Actor alec = actorService.createUser("alec").getActor();
        Actor blec = actorService.createUser("blec").getActor();
        ChessGame game = gameService.createGame(alec, blec);
        System.out.println(game.getId());
        ChessMove move = gameService.createMove(game, gameService.getFieldId("e2"), gameService.getFieldId("e4"));
        gameService.applyMove(move);
    }
    
}
