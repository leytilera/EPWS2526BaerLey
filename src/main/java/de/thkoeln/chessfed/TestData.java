package de.thkoeln.chessfed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.thkoeln.chessfed.services.IActorService;

@Component
public class TestData implements CommandLineRunner {

    private IActorService actorService;

    @Autowired
    public TestData(IActorService actorService) {
        this.actorService = actorService;
    }

    @Override
    @Profile("!prod")
    public void run(String... args) throws Exception {
        actorService.createUser("alec");
    }
    
}
