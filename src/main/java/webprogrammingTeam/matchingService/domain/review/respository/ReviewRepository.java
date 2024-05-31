package webprogrammingTeam.matchingService.domain.review.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.board.entity.Board;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBoard(Board board);
}
