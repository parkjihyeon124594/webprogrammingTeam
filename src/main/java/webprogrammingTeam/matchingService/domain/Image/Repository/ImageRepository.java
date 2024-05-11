package webprogrammingTeam.matchingService.domain.Image.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByBoard(Optional<Board> board);
}
