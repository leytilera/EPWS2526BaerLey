package de.thkoeln.chessfed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.thkoeln.chessfed.model.ILocalUserRepository;
import de.thkoeln.chessfed.model.LocalUser;

@Component
public class TestData implements CommandLineRunner {

    private ILocalUserRepository userRepository;

    @Autowired
    public TestData(ILocalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Profile("!prod")
    public void run(String... args) throws Exception {
        LocalUser user = new LocalUser();
        user.setUsername("alec");
        userRepository.save(user);
    }
    
}
