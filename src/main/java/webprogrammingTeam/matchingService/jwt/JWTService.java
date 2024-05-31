package webprogrammingTeam.matchingService.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JWTService {

    private SecretKey secretKey;

    private final Long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60L;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14L;


    public JWTService(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey=new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getEmail(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category",String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


    public String createAccessJwt(String email,String role){

        return Jwts.builder()
                .claim("category","accessToken")
                .claim("email",email)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
    public String createRefreshToken(String email,String role) {
        return Jwts.builder()
                .claim("category","refreshToken")
                //.claim("UUID", UUID)
                .claim("email",email)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }



    /**
     *
     * @param key key값
     * @param value jwt가 들어갈 value값
     * @return
     */

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키의 생명 주기
        //cookie.setSecure(true); // https 통신을 진행할 경우 이것을 활성화하면됨.
        cookie.setPath("/googleLogin"); // redirect로 쿠키를 만들어주는 주소.

        cookie.setHttpOnly(true); // true 설정 시 프론트 js에서 해당 쿠키에 접근하지 못하게 막음.
        // 키 밸류 형태로 : set-cookie로 담김
        return cookie;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
}
