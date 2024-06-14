package webprogrammingTeam.matchingService.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.member.dto.request.MemberCreateRequest;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

@RestController
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> signup(
            @RequestBody MemberCreateRequest memberCreateRequest
            ){
        Long id = memberService.signUpMember(memberCreateRequest);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,id));
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
