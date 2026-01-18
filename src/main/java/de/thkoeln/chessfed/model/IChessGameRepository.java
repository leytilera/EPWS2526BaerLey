package de.thkoeln.chessfed.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChessGameRepository extends JpaRepository<ChessGame, UUID> {
    
    List<ChessGame> getAllByWhitePlayerOrBlackPlayer(Actor whitePlayer, Actor blackPlayer);

    Optional<ChessGame> getByFederation(FederatedObject federation);

}
