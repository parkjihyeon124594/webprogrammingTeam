package webprogrammingTeam.matchingService.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
