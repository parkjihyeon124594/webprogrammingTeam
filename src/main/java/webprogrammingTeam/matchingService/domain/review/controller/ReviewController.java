package webprogrammingTeam.matchingService.domain.review.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewUpdateRequest;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.review.service.ReviewService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/board/{boardId}/review")
public class ReviewController {

    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    /**
     * 리뷰 작성
     * @param boardId 게시물 아이디
     * @param reviewSaveRequest 리뷰 정보
     * @param authentication 유저 정보
     * @return 리뷰 아이디
     */
    @PostMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> creatReview(
            @PathVariable("boardId") Long boardId,

            @RequestBody() ReviewSaveRequest reviewSaveRequest,
            Authentication authentication

           ) throws IOException {

        /**principal  email 얻기**/
        String email = "hh";

        Long saveId = reviewService.saveReview( reviewSaveRequest, boardId, email );

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ReviewAllReadResponse>>> getAllReview(
            @PathVariable("boardId") Long boardId
    ){
        List<ReviewAllReadResponse> allReview = reviewService.findAllReview(boardId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allReview));
    }

    /** 없어도 될듯 ?**/
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<ReviewIdReadResponse>> getOneReview(
            @PathVariable("reviewId") Long reviewId
    )throws IOException{
        ReviewIdReadResponse reviewIdReadResponse = reviewService.findOneReview(reviewId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, reviewIdReadResponse));
    }

    /**
     * 리뷰 수정
     *
     * @param reviewId 리뷰 아이디
     * @param reviewUpdateRequest 리뷰 수정 정보
     * @return 리뷰 아이디
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest
            )throws IOException{
        Long updateId = reviewService.updateReview(reviewId, reviewUpdateRequest);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, updateId));
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 리뷰 아이디
     * @return ok
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteReview(
            @PathVariable("reviewId") Long reviewId){
        reviewService.deleteOneReview( reviewId);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }



}
