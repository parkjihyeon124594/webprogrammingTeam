package webprogrammingTeam.matchingService.auth.handler.Itself;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTService jwtService;
    private final RefreshService refreshService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails itselfMember = (PrincipalDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = itselfMember.getAuthorities();

        String email = itselfMember.getEmail();
        String role = authorities.iterator().next().getAuthority();

        String accessToken = jwtService.createAccessJwt(email, role);
        String refreshToken =  jwtService.createRefreshToken(email,role);
        refreshService.saveRefreshEntity(email,refreshToken,role);


        response.setHeader("Accesstoken", accessToken);
        response.setHeader("Refreshtokn",refreshToken);

        log.info("자체 로그인에 성공하였습니다. 이메일 : {}",  email);
        log.info("자체 로그인에 성공하였습니다. Access Token : {}",  accessToken);
        log.info("자체 로그인에 성공하였습니다. Refresh Token : {}",  refreshToken);
    }

}
