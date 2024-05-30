package webprogrammingTeam.matchingService.auth.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.refresh.repository.RefreshRepository;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.jwt.JWTService;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTService jwtService;
    private final RefreshRepository refreshRepository;
    private final RefreshService refreshService;


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){

        // refresh token 획득
        String refreshToken =null;

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("refreshToken")){
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null){
            //response status code
            return new ResponseEntity<>("refresh token njull", HttpStatus.BAD_REQUEST);
        }

        // 만료 기간 확인
        try{
            jwtService.isExpired(refreshToken);
        }catch(ExpiredJwtException e){
            return new ResponseEntity<>("refresh token expired",HttpStatus.BAD_REQUEST);
        }

        //토큰이 refresh token인지 확인( 발급시 페이로드에 명시)
        String category = jwtService.getCategory(refreshToken);

        if(!category.equals("refreshToken")){
            return new ResponseEntity<>("invalid refresh token",HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);

        if(!isExist){
            return new ResponseEntity<>("invalid refresh token",HttpStatus.BAD_REQUEST);
        }

        //모든 검증이 끝난 후 해당 토큰에서 username과 role를 꺼내서 새로운 access token을 발급

        String email = jwtService.getEmail(refreshToken);
        String role =jwtService.getRole(refreshToken);

        String newAccess = jwtService.createAccessJwt(email,role);
        String newRefresh = jwtService.createRefreshToken(email,role);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshService.deleteRefreshEntity(refreshToken);
        refreshService.saveRefreshEntity(email,newRefresh,role);

        //response
        response.setHeader("accessToken",newAccess);
        response.addCookie(jwtService.createCookie("refreshToken=", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
