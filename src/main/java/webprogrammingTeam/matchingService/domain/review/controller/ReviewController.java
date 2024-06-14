package webprogrammingTeam.matchingService.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
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
@RequestMapping("/program/{programId}/review")
@Tag(name = "리뷰", description = "게시글의 리뷰 관련 Api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    /**
     * 리뷰 작성
     * @param programId 게시물 아이디
     * @param reviewSaveRequest 리뷰 정보

     * @return 리뷰 아이디
     */
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> creatReview(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody() ReviewSaveRequest reviewSaveRequest
           ) throws IOException {
        Long saveId = reviewService.saveReview(reviewSaveRequest, programId, principalDetails.getEmail());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping("/view")
    @Operation(summary = "한 게시글 관련 리뷰 전체 조회", description = "한 게시글에 등록된 모든 리뷰 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ReviewAllReadResponse>>> getAllReview(
            @PathVariable("programId") Long programId
    ){
        List<ReviewAllReadResponse> allReview = reviewService.findAllReview(programId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allReview));
    }


    @GetMapping("/view/{reviewId}")
    @Operation(summary = "리뷰 한 개 조회", description = "리뷰 수정할 때 필요")
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "리뷰 수정", description = "리뷰 수정하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest
            )throws IOException{
        Long updateId = reviewService.updateReview(reviewId, reviewUpdateRequest,principalDetails.getEmail());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, updateId));
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 리뷰 아이디
     * @return ok
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제 하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        reviewService.deleteOneReview( reviewId, principalDetails.getEmail());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }



}
