package webprogrammingTeam.matchingService.domain.message.handler;

import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.dto.MessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageHandler {
    private final MessageService messageService;
    private final MemberChannelSubscriptionService memberChannelSubscriptionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageHandler(MessageService messageService,
                          MemberChannelSubscriptionService memberChannelSubscriptionService,
                          SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
        this.messagingTemplate = messagingTemplate;
    }
    // public과 private를 나누면 된다. 그러면 sub은 관리 안 해도 된다.

    // 공개 채널에 보내는 메세지 처리, sender가 ban 안 되어있으면 메세지를 보내고 아니면 클라이언트에 오류 메세지 돌려줌.
    @MessageMapping("/chat/public/{channelId}") // ban을 추가해서 못 보내게?
    public void handlePublicMessage(@DestinationVariable Long channelId, @Payload MessagePayLoad messagePayLoad) {
        Long senderId = messagePayLoad.getSenderId();

        if (!memberChannelSubscriptionService.isBanned(channelId, senderId)) {
            sendMessage(channelId, senderId, messagePayLoad.getContent());
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You have been banned from this channel.");
        }

    }

    @MessageMapping("/chat/private/{channelId}") // kick을 추가해서?
    public void handlePrivateMessage(@DestinationVariable Long channelId, @Payload MessagePayLoad messagePayLoad) {
        Long senderId = messagePayLoad.getSenderId();

        sendMessage(channelId, senderId, messagePayLoad.getContent());
    }

    private void sendMessage(Long channelId, Long senderId, String content) {
        MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, content);
        messagingTemplate.convertAndSend("/topic/chat/" + channelId, savedMessageDTO);
    }

    // 강퇴 처리. 클라이언트에 메세지 송신. 아직 미구현
    private void handleKickRequest(Long channelId, Long senderId, Long targetMemberId) {
        // 권한을 어떻게?
        memberChannelSubscriptionService.kickMember(channelId, targetMemberId);
        messagingTemplate.convertAndSend("/topic/chat/" + channelId, "Member " + targetMemberId + " has been kicked from the channel.");
        messagingTemplate.convertAndSend("/topic/errors/" + targetMemberId, "You have been kicked from the channel " + channelId + ".");
    }
}

