package webprogrammingTeam.matchingService.domain.Review.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.Board.Dto.Request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;
import webprogrammingTeam.matchingService.domain.Review.Dto.Request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.domain.Review.Repository.ReviewRepository;
import webprogrammingTeam.matchingService.domain.Review.Service.ReviewService;
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


}
