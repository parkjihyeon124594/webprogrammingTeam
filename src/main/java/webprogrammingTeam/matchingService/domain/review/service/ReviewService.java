package webprogrammingTeam.matchingService.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewUpdateRequest;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.review.entity.Review;
import webprogrammingTeam.matchingService.domain.review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;


    public Long saveReview(ReviewSaveRequest reviewSaveRequest,Long programId){
        log.info("saveReview {}", programId);
       // Member member = memberRepository.findByEmail(email).orElseThrow(()-> new IllegalIdentifierException("회원을 찾을 수 없습니다."));
        Program program = programRepository.findById(programId).orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Review review = Review.builder()
                .title(reviewSaveRequest.title())
                .rating(reviewSaveRequest.rating())
                .content(reviewSaveRequest.content())
                .date(writingTimeToString(LocalDateTime.now()))
               // .member(member)
                .program(program)
                .build();

        reviewRepository.save(review);

        log.info("review.getId {}",String.valueOf(review.getId()));
        return review.getId();

    }

    public List<ReviewAllReadResponse> findAllReview(Long programId){
        try{

            Program program = programRepository.findById(programId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
            List<Review> reviewList = reviewRepository.findByProgram(program);

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
    public static String writingTimeToString(LocalDateTime writingTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return writingTime.format(formatter);
    }

}
