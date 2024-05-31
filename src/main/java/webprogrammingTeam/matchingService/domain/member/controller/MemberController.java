package webprogrammingTeam.matchingService.domain.member.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import webprogrammingTeam.matchingService.domain.member.dto.response.MemberIdReadResponse;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;

@RestController
@RequestMapping("/member")
public class MemberController {

    private MemberService memberService;
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<MemberIdReadResponse>> getMember(
            @PathVariable("memberId") Long memberId
    )throws IOException {

        MemberIdReadResponse memberIdReadResponse = memberService.findOneMember(memberId) ;
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, memberIdReadResponse));
    }



}
