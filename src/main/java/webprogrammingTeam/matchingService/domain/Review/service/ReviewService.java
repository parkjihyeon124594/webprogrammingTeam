package webprogrammingTeam.matchingService.domain.Review.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.Review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.Review.dto.request.ReviewUpdateRequest;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.Review.entity.Review;
import webprogrammingTeam.matchingService.domain.Review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
import webprogrammingTeam.matchingService.domain.board.repository.BoardRepository;
import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    LocalDateTime currentTime = LocalDateTime.now();



    public Long saveReview(ReviewSaveRequest reviewSaveRequest,Long boardId, String email){

        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalIdentifierException("회원을 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Review review = Review.builder()
                .rating(reviewSaveRequest.rating())
                .content(reviewSaveRequest.content())
                .date(String.valueOf(currentTime))
                .user(user)
                .board(board)
                .build();

        reviewRepository.save(review);


        return review.getId();

    }

    public List<ReviewAllReadResponse> findAllReview(Long boardId){
        try{

            Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
            List<Review> reviewList = reviewRepository.findByBoard(board);

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
    public Long updateReview( Long reviewId, ReviewUpdateRequest reviewUpdateRequest)throws IOException{
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();

        review.reviewUpdate(reviewUpdateRequest);
        return review.getId();
    }

    @Transactional
    public void deleteOneReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();
        reviewRepository.delete(review);
    }
}
