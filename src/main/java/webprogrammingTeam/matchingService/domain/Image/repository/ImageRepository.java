package webprogrammingTeam.matchingService.domain.Image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByBoard(Optional<Board> board);
}
