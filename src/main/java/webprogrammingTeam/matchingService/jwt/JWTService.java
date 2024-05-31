package webprogrammingTeam.matchingService.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTService {

    private SecretKey secretKey;

    private final Long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60L;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14L;

    private final MemberRepository memberRepository;

    public JWTService(MemberRepository memberRepository, @Value("${spring.jwt.secret}") String secret) {
        this.memberRepository = memberRepository;
        this.secretKey=new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
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


    public String createJwt(String category,String username,String role,Long expiredMs){

        return Jwts.builder()
                .claim("category",category)
                .claim("username",username)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }


    /**
     *
     * @param key key값
     * @param value jwt가 들어갈 value값
     * @return
     */

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키의 생명 주기
        //cookie.setSecure(true); // https 통신을 진행할 경우 이것을 활성화하면됨.
        //cookie.setPath("/");
        cookie.setHttpOnly(true); // 프론트 js에서 해당 쿠키에 접근하지 못하도록 막아둬야됨.

        return cookie;
    }
}
