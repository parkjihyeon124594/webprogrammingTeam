package webprogrammingTeam.matchingService.domain.message.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.dto.PrivateMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.dto.PublicMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import webprogrammingTeam.matchingService.global.exception.GlobalException;
import webprogrammingTeam.matchingService.jwt.JWTService;





@Controller
@Tag(name = "메세지 핸들러", description = "stomp(websocket)을 통해 받은 메세지를 처리하는 핸들러")
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;
    private final JWTService jwtService;

    @MessageMapping("/chat/public/{channelId}")
    @Operation(summary = "공개 채널의 메세지 처리", description = "공개 채널의 메세지를 처리하는 로직")
    public void handlePublicMessage(@DestinationVariable("channelId") Long channelId,
                                    @Payload PublicMessagePayLoad publicMessagePayLoad
    ) {
        String senderEmail =jwtService.getEmail(publicMessagePayLoad.Accesstoken());

        Member member = memberRepository.findByEmail(senderEmail).orElseThrow(()-> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));
        Long senderId = member.getId();


        log.info("1ㅋㅋ, {}",publicMessagePayLoad.content());
        MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, publicMessagePayLoad.content());
        sendPublicMessage(channelId, savedMessageDTO);

        log.info("2ㅋㅋ, {}",publicMessagePayLoad.content());
    }

    // 비공개 채널에 보내는 메시지 처리. subscription과 session의 조합해서 private를 구현해야 함.
    @MessageMapping("/chat/private/{channelId}")
    @Operation(summary = "비밀 채널의 메세지 처리", description = "비밀 채널의 메세지를 처리하는 로직")
    public void handlePrivateMessage(@DestinationVariable("channelId") Long channelId,
                                     @Payload PrivateMessagePayLoad privateMessagePayLoad) {

        String senderEmail =jwtService.getEmail(privateMessagePayLoad.Accesstoken());

        Member member = memberRepository.findByEmail(senderEmail).orElseThrow(()-> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));
        Long senderId = member.getId();

        MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, privateMessagePayLoad.content());
        sendPrivateMessage(channelId, savedMessageDTO);
    }

    private void sendPublicMessage(Long channelId, MessageDTO savedMessageDTO) {
        messagingTemplate.convertAndSend("/topic/chat/public/" + channelId, savedMessageDTO);
    }

    private void sendPrivateMessage(Long channelId, MessageDTO savedMessageDTO) {
        messagingTemplate.convertAndSend("/topic/chat/private/" + channelId, savedMessageDTO);
    }

    private void sendErrorMessage(Long senderId) {
        messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You have to subscribe from this channel.");
    }

}

/*@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {
    private static final String TOPIC_DESTINATION_FORMAT = "/topic/chat/public/%d";
    private static final String ERROR_MESSAGE = "Failed to handle message for channel: {}";

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    @MessageMapping("/chat/public/{channelId}")
    public void handleMessage(@DestinationVariable Long channelId,
                              @Payload PublicMessagePayLoad publicMessagePayload) {
        try {
            String senderEmail = publicMessagePayload.senderEmail();
            MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderEmail, publicMessagePayload.content());

            sendMessage(channelId, savedMessageDTO);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, channelId, e);
        }
    }

    private void sendMessage(Long channelId, MessageDTO messageDTO) {
        log.info("Sending message to channel {}: {}", channelId, messageDTO.getContent());
        String destination = String.format(TOPIC_DESTINATION_FORMAT, channelId);
        messagingTemplate.convertAndSend(destination, messageDTO);
    }
}*/
