package webprogrammingTeam.matchingService.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.user.entity.Role;
import webprogrammingTeam.matchingService.domain.user.entity.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;


@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePathLists = {"/login", "/favicon.ico",
                "/oauth2/authorization/google", "/login/oauth2/code/google"};
        String path = request.getRequestURI();

        if (path.startsWith("/auth") || path.startsWith("/v3")) {
            return true;
        }

        return Arrays.stream(excludePathLists).
                anyMatch((excludePath) -> excludePath.equals(path));
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        log.info("JWT FILTER도 실행이 되나?");
        //헤더에서 access 키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("accessToken");

        // access token이 없다면 다음 필터로 넘김
        if(accessToken ==null){
            filterChain.doFilter(request,response);
            return;
        }

        /*// 토큰 만료 여부 확인
        // 만료시 다음 필터로 넘기지 않고 status code 반환

        try{
            jwtService.isExpired(accessToken);
        }catch (ExpiredJwtException e){

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // 토큰이 access 인지 확인 (발급 시 페이로드에 명시함)
        String category = jwtService.getCategory(accessToken);

        if(!category.equals("access")){

            // response body
            PrintWriter writer =response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }*/

        if(jwtService.validateToken(accessToken) == false){
            // response body
            PrintWriter writer =response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 모든 토큰 검증을 마친 후 access token이 정상적이라면 username, role 값 획득

        String email = jwtService.getEmail(accessToken);
        String username = jwtService.getUsername(accessToken);
        String role = jwtService.getRole(accessToken);

        User user = User.builder()
                .userName(username)
                .email(email)
                .role(Role.valueOf(String.valueOf(role)))
                .build();

        // SecurityContextHolder에서 인증 정보 가져오기
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails currentUser = (PrincipalDetails) currentAuth.getPrincipal();

        /*if (currentAuth != null) {
            PrincipalDetails currentUser = (PrincipalDetails) currentAuth.getPrincipal();
            log.info("Current User Email: {}", currentUser.getEmail());
        } else {
            log.warn("SecurityContextHolder에 인증 정보가 없습니다.");
        }*/

        //토큰 검증 완료 and SecurityContextHolder에 유저 정보가 저장된 상태
        if(currentUser.getEmail().equals(user.getEmail()) && currentAuth!=null){
            log.info("토큰 검증 완료 and SecurityContextHolder에 유저 정보가 저장된 상태");
        }
        else{
            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
/*        // User 객체를 생성하고, PrincipalDetails 객체를 생성함.
        // 그 후, UsernamePasswordAuthenticationToken에 넣어서 로그인을 진행하면됨.
        PrincipalDetails principalDetails = new PrincipalDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 최종적으로 SecurityContextHolder에 유저의 세션을 등록시킴.
        SecurityContextHolder.getContext().setAuthentication(authToken);*/

        filterChain.doFilter(request, response);


    }
}
