package webprogrammingTeam.matchingService.domain.message.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.channel.dto.CreateChannelRequest;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.global.exception.GlobalException;
import webprogrammingTeam.matchingService.jwt.JWTService;


@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JWTService jwtService;
    private final MemberRepository memberRepository;
    private final ChannelService channelService;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("들어오지롤ㅇ ㅋㅋ ");
        String email = null; // email 변수를 블록 밖에서 선언

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String Accesstoken = accessor.getFirstNativeHeader("Accesstoken2");
            log.info("Accesstoken, {}", Accesstoken);

            String bearerToken = Accesstoken.trim();
            if (!bearerToken.trim().isEmpty() && bearerToken.startsWith("Bearer ")) {
                Accesstoken = bearerToken.substring(7);
                log.info("bearerToken 에 들어옴. {}",Accesstoken);
            }
            if (!jwtService.validateToken(Accesstoken)) {
                log.info("access token이 필요합니다.");
                return message; // 토큰이 유효하지 않으면 메시지를 그대로 반환하고 종료
            }

            email = jwtService.getEmail(Accesstoken);
            log.info("email2, {}", email);
            accessor.addNativeHeader("senderEmail", email);
        }
        log.info("email3, {}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));


        PrincipalDetails principalDetails = new PrincipalDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 최종적으로 SecurityContextHolder에 유저의 세션을 등록시킴.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        CreateChannelRequest createChannelRequest = CreateChannelRequest.builder()
                .title("안녕하세요 컴퓨터 배워가세요\n").build();

        Channel publicChannel = channelService.createPublicChannel(createChannelRequest.getTitle());

        return message;
    }

}
