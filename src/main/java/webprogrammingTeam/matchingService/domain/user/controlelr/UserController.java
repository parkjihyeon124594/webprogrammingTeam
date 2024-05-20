package webprogrammingTeam.matchingService.domain.user.controlelr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.global.util.ApiUtil;
import webprogrammingTeam.matchingService.jwt.JWTFilter;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    @PostMapping
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> test(){
        log.info("컨트롤러 jwt 필터 테스트");
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> test2(){
        log.info("컨트롤러 jwt 필터 테스트");

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }
}
