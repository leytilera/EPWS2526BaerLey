package de.thkoeln.chessfed.model;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ILocalUserRepository extends JpaRepository<LocalUser, UUID> {
    
    Optional<LocalUser> getByUsername(String username);

    Optional<LocalUser> getByActor(Actor actor);

}
