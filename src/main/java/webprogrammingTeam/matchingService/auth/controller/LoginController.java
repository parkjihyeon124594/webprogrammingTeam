package webprogrammingTeam.matchingService.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.member.dto.request.MemberLoginRequest;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.global.exception.GlobalException;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @PostMapping()
    public ResponseEntity<String> login(
            @RequestBody MemberLoginRequest memberLoginRequest){


        Member member = memberRepository.findByEmail(memberLoginRequest.email())
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 입력된 비밀번호와 저장된 암호화된 비밀번호 비교
        if (passwordEncoder.matches(memberLoginRequest.password(), member.getPassword())) {
            // 로그인 성공
            return ResponseEntity.ok("로그인 성공");
        } else {
            // 비밀번호가 일치하지 않을 경우
            throw new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND);
        }


    }
}
