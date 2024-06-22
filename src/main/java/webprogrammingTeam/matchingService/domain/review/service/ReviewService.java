package webprogrammingTeam.matchingService.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.recruitment.repository.RecruitmentRepository;
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
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final RecruitmentRepository recruitmentRepository;

    public Long saveReview(ReviewSaveRequest reviewSaveRequest,Long programId, String email) throws AccessDeniedException {

        Program program = programRepository.findById(programId)
                .orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));


        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("ReviewService/saveReview/member을 찾을 수 없습니다."));

        log.info("user ID {}, program ID{}", member.getId(), program.getId());
        log.info("recruitmentRepository.findByProgramIdAndMemberId(program.getId(), member.getId())::::{}",recruitmentRepository.findByProgramIdAndMemberId(program.getId(), member.getId()).getId());
        //참여자였던 사람만 review 작성 가능
        if(recruitmentRepository.findByProgramIdAndMemberId(program.getId(), member.getId()) == null)
        {
            throw new AccessDeniedException("참여한 프로그램에 리뷰를 쓸 수 있음.");
        }


        // 모집 기간 이후인지 확인
        if(LocalDateTime.now().plusHours(9).isBefore(convertStringToLocalDateTime(program.getProgramDate()))) {
            throw new AccessDeniedException("프로그램 참여를 한 후에 리뷰를 작성할 수 있습니다.");
        }

        if(reviewRepository.findByProgramIdAndMemberId(programId, member.getId()) != null){
            throw new AccessDeniedException("리뷰를 이미 작성하였습니다");
        }

        Review review = Review.builder()
                .title(reviewSaveRequest.title())
                .rating(reviewSaveRequest.rating())
                .content(reviewSaveRequest.content())
                .date(writingTimeToString(LocalDateTime.now()))
                .member(member)
                .program(program)
                .build();

        reviewRepository.save(review);

        log.info("review.getId {}",String.valueOf(review.getId()));
        return review.getId();

    }

    //시간 포함
    private LocalDateTime convertStringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("옳지 않은 데이터 형식: " + dateStr);
            return null;
        }
    }
//    private LocalDate convertStringToLocalDate(String dateStr) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        try {
//            return LocalDate.parse(dateStr, formatter);
//        } catch (DateTimeParseException e) {
//            System.err.println("옳지 않은 데이터 형식: " + dateStr);
//            return null;
//        }
//    }

    public List<ReviewAllReadResponse> findAllReview(Long programId){
        try{

            Program program = programRepository.findById(programId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
            List<Review> reviewList = reviewRepository.findByProgram(program);

            List<ReviewAllReadResponse> responseList = new ArrayList<>();

            for(Review review : reviewList){
                responseList.add(
                        new ReviewAllReadResponse(review.getId(),review.getMember().getEmail(),review.getTitle(), review.getRating(), review.getContent(), review.getDate())
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
                .reviewId(review.getId())
                .title(review.getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .date(review.getDate())
                .build();
    }

    @Transactional
    public Long updateReview( Long reviewId, ReviewUpdateRequest reviewUpdateRequest, String email)throws IOException{
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();

        if(!review.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("본인만 리뷰를 수정할 수 있습니다.");
        }
        review.reviewUpdate(reviewUpdateRequest);
        return review.getId();
    }

    @Transactional
    public void deleteOneReview(Long reviewId, String email)throws IOException{
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow();
        if(!review.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("본인만 리뷰를 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }
    public static String writingTimeToString(LocalDateTime writingTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return writingTime.format(formatter);
    }

}
