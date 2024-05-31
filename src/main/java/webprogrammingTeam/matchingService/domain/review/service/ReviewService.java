package webprogrammingTeam.matchingService.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewUpdateRequest;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
import webprogrammingTeam.matchingService.domain.board.repository.BoardRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    LocalDateTime currentTime = LocalDateTime.now();



    public Long saveReview(ReviewSaveRequest reviewSaveRequest,Long boardId, String email){

        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new IllegalIdentifierException("회원을 찾을 수 없습니다."));
        Board board = boardRepository.findById(boardId).orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Review review = Review.builder()
                .rating(reviewSaveRequest.rating())
                .content(reviewSaveRequest.content())
                .date(String.valueOf(currentTime))
                .member(member)
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
