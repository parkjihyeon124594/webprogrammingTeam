package webprogrammingTeam.matchingService.auth.handler.OAuth;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    //UsernamePasswordAuthenticationFilter는 사용자 인증 과정을 담당하는 필터입
    //SimpleUrlAuthenticationSuccessHandler는 인증 성공 후 처리를 담당하는 핸들러

    //인증 성공 후 =>
    // 1.authentication 객체를 SecurityContextHolder에 담음
    // 2.refreshToken도 DB에 저장
    // 3.response의 cookie에 refreshToken을 담아서 보냄
    // 4.redirect로 보내고 => 다시 그 redirect 주소로 http 요청을 보냄
    // 5.refresh Token을 검증하고 access token을 헤더에 담아서 발급



    private final JWTService jwtService;
    private final RefreshService refreshService;

    /**
     *
     *  CustomOAuth2UserService의 loadUser 메서드에서 Principal 객체를 반환하면,
     *  이 객체가 Authentication 객체에 저장됨.
     *  더 자세히 설명하면, OAuth2 로그인 과정에서 loadUser 메서드가 반환하는 객체는 Authentication 객체의 Principal로 설정됨.
     *
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities =oAuth2User.getAuthorities();


        String email = oAuth2User.getEmail();
        String role = authorities.iterator().next().getAuthority();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Print all attributes
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            log.info("Attribute key: {}, value: {}", entry.getKey(), entry.getValue());
        }
        //String accessToken = jwtService.createAccessJwt(email, role);

        String refreshToken =  jwtService.createRefreshToken(email,role);
        refreshService.saveRefreshEntity(email,refreshToken,role);

        //Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, authorities);

        Authentication storedAuth = SecurityContextHolder.getContext().getAuthentication();
        if (storedAuth != null && storedAuth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails storedUser = (PrincipalDetails) storedAuth.getPrincipal();
            log.info("SecurityContextHolder에 저장된 Authentication 객체 확인: 이메일 = {}", storedUser.getEmail());
        } else {
            log.warn("SecurityContextHolder에 Authentication 객체가 저장되지 않았습니다.");
        }
        //response.setHeader("Authorization-Accesstoken", accessToken);
        // 최종적으로 SecurityContextHolder에 유저의 세션을 등록시킴.
        //SecurityContextHolder.getContext().setAuthentication(authToken);



        // SecurityContextHolder에 저장된 Authentication 객체 확인



        response.addCookie(jwtService.createCookie("refreshToken", refreshToken));
        response.sendRedirect("http://localhost:3000/googleLogin");

        log.info("CustomOAuth2SuccessHandler : OAuth2 로그인에 성공하였습니다. 이메일 : {}",  oAuth2User.getEmail());
        //log.info("CustomOAuth2SuccessHandler : OAuth2 로그인에 성공하였습니다. Access Token : {}",  accessToken);
        log.info("CustomOAuth2SuccessHandler : OAuth2 로그인에 성공하였습니다. Refresh Token : {}",  refreshToken);


    }

    }

