package webprogrammingTeam.matchingService.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    //UsernamePasswordAuthenticationFilter는 사용자 인증 과정을 담당하는 필터

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshService refreshService;

    private final Long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60L;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14L;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @param authentication Authentication 객체에서 현재 세션에 로그인된 사용자
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

        // authorities는 GrantedAuthority 객체의 컬렉션으로, 사용자의 역할 또는 권한을 나타냅니다.
        //이 경우 컬렉션은 GrantedAuthority 인터페이스를 구현하는 모든 객체를 저장할 수 있습니다.
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();

        String email = oAuth2User.getEmail();
        String role = authorities.iterator().next().getAuthority();

        //토큰 생성
        String accessToken = jwtService.createAccessJwt(email, role);
        String refreshToken = jwtService.createRefreshToken(email,role);

        //Refresh 토큰 저장
        refreshService.saveRefreshEntity(email,refreshToken,role);

        Cookie cookie=jwtService.createCookie("refreshToken",refreshToken);

        //응답 설정
        response.setHeader("accessToken", accessToken); // 헤더에 key,value 형태로 넣음
        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());


        log.info("LoginFilter : OAuth2 로그인에 성공하였습니다. 이메일 : {}",  oAuth2User.getEmail());
        log.info("LoginFilter : OAuth2 로그인에 성공하였습니다. Access Token : {}",  accessToken);
        log.info("LoginFilter : OAuth2 로그인에 성공하였습니다. Refresh Token : {}",  refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }


}
