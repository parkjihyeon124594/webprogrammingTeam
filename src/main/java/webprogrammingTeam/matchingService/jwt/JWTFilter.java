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
import webprogrammingTeam.matchingService.domain.member.entity.Role;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

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

                "/oauth2/authorization/google", "/login/oauth2/code/google","/member/signup","/login", "/program/view/**","/program/category/**"};

        String path = request.getRequestURI();

        if (path.startsWith("/auth") || path.startsWith("/v3")) {
            return true;
        }

        return Arrays.stream(excludePathLists).
                anyMatch((excludePath) -> excludePath.equals(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 access 키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Accesstoken");
        log.info("jwt doFilterInternal access token : {} ",accessToken);

        /*
         요청이 들어오면 헤더 토큰 프로바이더로 사용하는데, SecurityContextHolder는 jwt를 단순히 디코더하는 역할을 함
         SecurityContextHolder는 요청이 시작할때부터 끝날때까지만, 하는거임
         토큰 기반 유지에서는 로그인 유지를 하면 안됨
         서버는 요청 처리 단건에 대한 플로우가 같아야된 statelss 해야함.
         로그인을 하면 엑세스 토큰과 리프레시 토큰을 만들고, "로그인 유지는 프론트 측에서 해야함.
         서버는 토큰을 받아서 디코더해서 인증 작업을 하는 것만 하는 역할을 해야됨. 어쎈틱케이션을 한 유저인지 아닌지를 판별하기 위한 역할을 함."

         SecurityContextHolder를 쓰레드 로컬에 사용하는 이유는 동시성 문제가 발생할 가능성이 있기 때문이다. => 어쎈틱케이션은 공유자원이 아니라 독립적인 공간에 넣는 이유임
         쓰레드 로컬마다 다른 커넥션을 가지고 있기 때문에, 비동기 처리를 하면 롤백이 인됨.

        */



        // SecurityContextHolder에 저장된 Authentication 객체 확인
/*
        Authentication storedAuth = SecurityContextHolder.getContext().getAuthentication();

        if (storedAuth != null && storedAuth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails storedUser = (PrincipalDetails) storedAuth.getPrincipal();
            log.info("SecurityContextHolder에 저장된 Authentication 객체 확인: 이메일 = {}", storedUser.getEmail());
        } else {
            log.warn("SecurityContextHolder에 Authentication 객체가 저장되지 않았습니다.");
        }
*/

        //헤더에서 access 키에 담긴 토큰을 꺼냄
        //String accessToken = request.getHeader("accessToken");


        // access token이 없다면 다음 필터로 넘김
        if(accessToken ==null){
            filterChain.doFilter(request,response);
            return;
        }

        // 토큰 만료 여부 확인
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

        if(!category.equals("accessToken")){

            // response body
            PrintWriter writer =response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        // 모든 토큰 검증을 마친 후 access token이 정상적이라면 username, role 값 획득

        String email = jwtService.getEmail(accessToken);
        String role = jwtService.getRole(accessToken);
        log.info("jwt filer 의 이메일 {} ",email);

        Member member = Member.builder()
                .email(email)
                .role(Role.valueOf(String.valueOf(role)))
                .build();

        // SecurityContextHolder에서 인증 정보 가져오기
        /*Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails currentUser = (PrincipalDetails) currentAuth.getPrincipal();
*/
        // User 객체를 생성하고, PrincipalDetails 객체를 생성함.
        // 그 후, UsernamePasswordAuthenticationToken에 넣어서 로그인을 진행하면됨.

        PrincipalDetails principalDetails = new PrincipalDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 최종적으로 SecurityContextHolder에 유저의 세션을 등록시킴.
        SecurityContextHolder.getContext().setAuthentication(authToken);



        filterChain.doFilter(request, response);


    }
}
