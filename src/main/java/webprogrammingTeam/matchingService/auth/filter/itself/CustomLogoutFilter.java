package webprogrammingTeam.matchingService.auth.filter.itself;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import webprogrammingTeam.matchingService.domain.refresh.repository.RefreshtokenRepository;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTService jwtService;
    private final RefreshtokenRepository refreshtokenRepository;
    @Override
    //dofilter에서 public과 private으로 두 번 선언해서 public에서 private을 꺼내쓰는 방식의 이유
    // 1.(HttpServletRequest) 와 (HttpServletResponse) 캐스팅하기 편리하게 만든 부분도 있고 private으로 로직 자체를 보호하기 위함
    // 2.대부분의 스프링 필터 관련 코드를 뜯어 보시면 public doFilter와 private doFilter 형태로 구현되음.
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        log.info("custom logout filer test refersh token 1 {}",request.getHeader("Refreshtoken"));

        // logout path and method verify
        String requestUri = request.getRequestURI();
        if(!requestUri.matches("^\\/logout$")){

            filterChain.doFilter(request,response);
            return;
        }

        String requestMethod = request.getMethod();
        if(!requestMethod.equals("POST")){

            filterChain.doFilter(request,response);
            return;
        }

        // get refresh token
        String refresh =request.getHeader("Refreshtoken");


        // refresh null check
        if(refresh == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        // expired check
        try{
            jwtService.isExpired(refresh);
        }catch (ExpiredJwtException e){
            // reponse status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        // 토큰이 refesh인지 확인( 발급 시에 페이로드에 명시돼 있음)
        String category = jwtService.getCategory(refresh);
        if(!category.equals("Refreshtoken")){


            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 저장돼 있는지 확인
        Boolean isExist = refreshtokenRepository.existsByRefresh(refresh);
        if (!isExist) {// DB에 없다면 이미 로그아웃이 된 상태이기 때문에 더 이상 진행하지 않음.

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        refreshtokenRepository.deleteByRefresh(refresh);

        response.setStatus(HttpServletResponse.SC_OK);


    }

}
