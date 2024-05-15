package webprogrammingTeam.matchingService.domain.Review.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.Review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
