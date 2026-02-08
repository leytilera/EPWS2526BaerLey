package de.thkoeln.chessfed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FederatedChessApplication {

	public static void main(String[] args) {
		SpringApplication.run(FederatedChessApplication.class, args);
	}

}
