package webprogrammingTeam.matchingService.auth.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccessTokenController {

    private final JWTService jwtService;

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final AuthenticationManager authenticationManager;

    //@ResponseBody 어노테이션을 사용하고 객체를 반환하면 HttpMessageConverter에 의해 객체가 Json으로 변환
    //ResponseEntity : HttpStatus, HttpHeaders, HttpBody 데이터를 갖는 ResponseEntity 클래스를 활용
    @GetMapping("/googleLogin")
    public ResponseEntity<ApiUtil.ApiSuccessResult<String>> accessTokenResponse(
            //@CookieValue(value="Authorization-refreshtoken", required = true)Cookie cookie
            HttpServletRequest request, HttpServletResponse response
    ) {

        //String refreshToken = cookie.getAttribute("Authorization-Refreshtoken");
        String refreshToken = getRefreshTokenFromCookie(request.getCookies());
        String accessToken = jwtService.createAccessJwt(jwtService.getEmail(refreshToken), jwtService.getRole(refreshToken));

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        headers.add("Set-Cookie", "refreshToken =" + refreshToken);

        return ResponseEntity.ok().headers(headers).body(ApiUtil.success(HttpStatus.OK, "Access token 발급 완료"));
    }

    private static String getRefreshTokenFromCookie(Cookie[] cookies) {
        String token = null;

        if (cookies == null) {
            return token;
        } else {
            token = Arrays.stream(cookies)
                    .filter(header -> header.getName().equals("Authorization-refreshtoken"))
                    .findAny()
                    .map(cookie -> cookie.getValue())
                    .orElse(null);

            return token;
        }
    }



}
