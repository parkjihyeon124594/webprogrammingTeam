package webprogrammingTeam.matchingService.domain.message.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
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


@Controller
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
            Long senderId = publicMessagePayload.senderId();
            MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, publicMessagePayload.content());

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
}


/*
@Controller
@Tag(name = "메세지 핸들러", description = "stomp(websocket)을 통해 받은 메세지를 처리하는 핸들러")
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {
    private final MessageService messageService;
    private final MemberChannelSubscriptionService memberChannelSubscriptionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;
//    @Autowired
//    public MessageHandler(MessageService messageService,
//                          MemberChannelSubscriptionService memberChannelSubscriptionService,
//                          SimpMessagingTemplate messagingTemplate) {
//        this.messageService = messageService;
//        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
//        this.messagingTemplate = messagingTemplate;
//    }
    // public과 private를 나누면 된다.

    // 공개 채널에 보내는 메시지 처리.
    @MessageMapping("/chat/public/{channelId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "공개 채널의 메세지 처리", description = "공개 채널의 메세지를 처리하는 로직")
    public void handlePublicMessage(@DestinationVariable("channelId") Long channelId,
                                    @Payload PublicMessagePayLoad publicMessagePayLoad,
                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("실행은 되고 있음" );
        String senderEmail = principalDetails.getEmail();

        Long senderId = memberRepository.findByEmail(senderEmail).get().getId();

        log.info("들어는 왔는데, {}", senderEmail);
        if (memberChannelSubscriptionService.isSubscriber(channelId, senderId)) {

            MessageDTO savedMessageDTO = messageService.addMessage(channelId, principalDetails, publicMessagePayLoad.content());
            sendPublicMessage(channelId, savedMessageDTO);
        }
        else {
            sendErrorMessage(senderId);
        }
    }

    // 비공개 채널에 보내는 메시지 처리. subscription과 session의 조합해서 private를 구현해야 함.
    @MessageMapping("/chat/private/{channelId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "비밀 채널의 메세지 처리", description = "비밀 채널의 메세지를 처리하는 로직")
    public void handlePrivateMessage(@DestinationVariable("channelId") Long channelId,
                                     @Payload PrivateMessagePayLoad privateMessagePayLoad,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {

        MessageDTO savedMessageDTO = messageService.addMessage(channelId, principalDetails, privateMessagePayLoad.getContent());
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
*/

