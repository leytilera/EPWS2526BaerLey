package de.thkoeln.chessfed.model;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChessGameRepository extends JpaRepository<ChessGame, UUID> {
    
}
