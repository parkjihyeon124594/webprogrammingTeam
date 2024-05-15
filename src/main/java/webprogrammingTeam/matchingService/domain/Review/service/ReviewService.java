package webprogrammingTeam.matchingService.domain.Review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.Review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.Review.entity.Review;
import webprogrammingTeam.matchingService.domain.Review.respository.ReviewRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<ReviewAllReadResponse> findAllReview(){
        try{
            List<Review> reviewList = reviewRepository.findAll();
            List<ReviewAllReadResponse> responseList = new ArrayList<>();

            for(Review review : reviewList){
                responseList.add(
                        new ReviewAllReadResponse(review.getRating(), review.getContent(), review.getDate())
                );
            }

            return responseList;
        }catch(Exception e){
        }
        return null;
    }

    public ReviewIdReadResponse findOneReview(Long id)throws IOException {
        Review review = reviewRepository.findById(id)
                .orElseThrow();

        return ReviewIdReadResponse.builder()
                .rating(review.getRating())
                .content(review.getContent())
                .date(review.getDate())
                .build();
    }

    @Transactional
    public void deleteOneReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();
        reviewRepository.delete(review);
    }


}
