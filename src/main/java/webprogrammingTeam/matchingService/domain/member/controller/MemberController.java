package webprogrammingTeam.matchingService.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

@RestController
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    @PostMapping
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> test(){
        log.info("컨트롤러 jwt 필터 테스트");
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> test2(
            //@AuthenticationPrincipal PricipalDetails pricipalDetails
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ){
        log.info("컨트롤러 jwt 필터 테스트");
        log.info("principalDetails : {}",principalDetails.getEmail());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }


}
