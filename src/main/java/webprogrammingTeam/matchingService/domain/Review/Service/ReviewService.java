package webprogrammingTeam.matchingService.domain.Review.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.Board.Dto.Request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;
import webprogrammingTeam.matchingService.domain.Review.Dto.Request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.Review.Entity.Review;
import webprogrammingTeam.matchingService.domain.Review.Repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    LocalDateTime currentTime = LocalDateTime.now();


    public Long saveReview(ReviewSaveRequest reviewSaveRequest){
        Review review = Review.builder()
                .rating(reviewSaveRequest.rating())
                .content(reviewSaveRequest.content())
                .date(String.valueOf(currentTime))
                .build();

        reviewRepository.save(review);


        return review.getId();

    }


}
