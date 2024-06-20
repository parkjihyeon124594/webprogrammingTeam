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
        log.info("stompHandler message {}",message);
        String email = null;

        // CONNECT 명령어에 대해서만 로그를 출력
        if (accessor.getCommand() == StompCommand.CONNECT || accessor.getCommand() == StompCommand.SUBSCRIBE || accessor.getCommand() == StompCommand.SEND ) {


            String accesstoken = accessor.getFirstNativeHeader("Accesstoken");
            log.info("Accesstoken received: {}", accesstoken);

            if (accesstoken != null && !accesstoken.trim().isEmpty() && accesstoken.startsWith("Bearer ")) {
                String bearerToken = accesstoken.trim().substring(7);

                if (jwtService.validateToken(bearerToken)) {
                    email = jwtService.getEmail(bearerToken);



                } else {

//                    accessor.addNativeHeader("senderEmail", email);

//                    Member member = memberRepository.findByEmail(email)
//                            .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));
//
//                    PrincipalDetails principalDetails = new PrincipalDetails(member);
//                    Authentication authToken = new UsernamePasswordAuthenticationToken(
//                            principalDetails, null, principalDetails.getAuthorities());
//
//                    SecurityContextHolder.getContext().setAuthentication(authToken);

                   } else {

                    log.info("Invalid access token.");
                }
            } else {
                log.info("Invalid token format.");
            }
        }

/*
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));


        PrincipalDetails principalDetails = new PrincipalDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 최종적으로 SecurityContextHolder에 유저의 세션을 등록시킴.
        SecurityContextHolder.getContext().setAuthentication(authToken);
*/

        return message;
    }

}
