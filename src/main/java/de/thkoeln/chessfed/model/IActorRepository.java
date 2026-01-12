package de.thkoeln.chessfed.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActorRepository extends JpaRepository<Actor, String> {
    
    Optional<Actor> getByLocalpartAndDomain(String localpart, String domain);

}
