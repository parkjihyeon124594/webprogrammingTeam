package webprogrammingTeam.matchingService.domain.Review.controller;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewIdReadResponse;
import webprogrammingTeam.matchingService.domain.Review.entity.Review;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardAllReadResponse;
import webprogrammingTeam.matchingService.domain.Review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.Review.dto.response.ReviewAllReadResponse;
import webprogrammingTeam.matchingService.domain.Review.respository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.Review.service.ReviewService;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/review")
public class ReviewController {

    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    @PostMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> creatReview(
            @RequestPart(value="reviewSaveRequest")ReviewSaveRequest reviewSaveRequest
           ) throws IOException {

        Long saveId = reviewService.saveReview(reviewSaveRequest);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ReviewAllReadResponse>>> getAllReview(){
        List<ReviewAllReadResponse> allReview = reviewService.findAllReview();
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allReview));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<ReviewIdReadResponse>> getOneReview(
            @PathVariable("reviewId") Long reviewId
    )throws IOException{
        ReviewIdReadResponse reviewIdReadResponse = reviewService.findOneReview(reviewId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, reviewIdReadResponse));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteReview(
            @PathVariable("reviewId") Long reviewId){
        reviewService.deleteOneReview(reviewId);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }



}
