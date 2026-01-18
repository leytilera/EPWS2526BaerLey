package de.thkoeln.chessfed.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChessMoveRepository extends JpaRepository<ChessMove, UUID> {
    
    List<ChessMove> getAllByGame(ChessGame game);

    Optional<ChessMove> getByGameAndMoveCount(ChessGame game, int moveCount);

}
