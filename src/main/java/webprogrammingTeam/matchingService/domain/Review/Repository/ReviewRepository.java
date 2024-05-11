package webprogrammingTeam.matchingService.domain.Review.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.Review.Entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
