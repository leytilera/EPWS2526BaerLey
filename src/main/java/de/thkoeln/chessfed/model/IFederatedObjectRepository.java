package de.thkoeln.chessfed.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFederatedObjectRepository extends JpaRepository<FederatedObject, String> {
    
}
