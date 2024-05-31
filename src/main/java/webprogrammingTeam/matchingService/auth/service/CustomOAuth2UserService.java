package webprogrammingTeam.matchingService.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import webprogrammingTeam.matchingService.auth.dto.GoogleResponse;
import webprogrammingTeam.matchingService.auth.dto.OAuth2DTO;
import webprogrammingTeam.matchingService.auth.dto.OAuth2Response;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        // 상위 클래스의 메서드를 호출하여 OAuth2User를 반환함.
        // OAuth2User : 소셜 로그인을 통해 얻은 사용자 정보를 담고 있는 객체
        OAuth2User oAuth2User =super.loadUser(userRequest);


        //resource server로 부터 받아온 user 정보(속성)가 담겨져 있음.
        OAuth2Response oAuth2Response=getOAuth2Response(oAuth2User);

        // oAuth2DTO 생성
        OAuth2DTO oAuth2DTO = createOAuth2DTO(oAuth2User, oAuth2Response);

        // oAuth2DTO 객체를 이용해서 DB에 유저 정보 저장

        return null;
    }
    /**
     *
     * @param oAuth2User resource server로 부터 받아온 user 정보(속성)
     * @return oAuth2Response
     */
    private static OAuth2Response getOAuth2Response(OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        return oAuth2Response;
    }

    /**
     * OAuth2DTo 생성
     *
     * @param oAuth2User
     * @param oAuth2Response
     * @return
     */
    private static OAuth2DTO createOAuth2DTO(OAuth2User oAuth2User, OAuth2Response oAuth2Response) {
        return OAuth2DTO.builder()
                .attributes(oAuth2User.getAttributes())
                .name(oAuth2Response.getName())
                .email(oAuth2Response.getEmail())
                .role("ROLE_MEMBER")
                .build();
    }



}
