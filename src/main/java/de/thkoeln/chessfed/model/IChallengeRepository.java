package de.thkoeln.chessfed.model;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChallengeRepository extends JpaRepository<Challenge, UUID> {
    
    Optional<Challenge> getByFederation(FederatedObject federation);

}
