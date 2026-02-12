package de.thkoeln.chessfed.model;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActivityErrorRepository extends JpaRepository<ActivityError, UUID> {
    
    Optional<ActivityError> getOneByActivityAndTarget(Activity activity, Actor target);

}
