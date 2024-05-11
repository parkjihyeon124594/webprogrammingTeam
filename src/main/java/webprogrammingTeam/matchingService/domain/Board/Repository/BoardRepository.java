package webprogrammingTeam.matchingService.domain.Board.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
