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

    @MessageMapping("/chat/{channelId}")
    public void handleMessage(@DestinationVariable Long channelId, @Payload MessagePayLoad messagePayLoad) {
        Long senderId = messagePayLoad.getSenderId();

        if (messagePayLoad.isSubscribeRequest()) {
            handleSubscriptionRequest(channelId, senderId);
        } else if (messagePayLoad.isKickRequest()) {
            handleKickRequest(channelId, senderId, messagePayLoad.getKickMemberId());
        } else {
            sendMessage(channelId, senderId, messagePayLoad.getContent());
        }
    }

    // 구독 처리. 구독 정보가 있으면 정상적으로 구독해주고, 아니면 클라이언트에 오류 돌려줌
    private void handleSubscriptionRequest(Long channelId, Long senderId) {
        if (memberChannelSubscriptionService.isSubscriber(channelId, senderId)) {
            messagingTemplate.convertAndSend("/topic/chat/" + channelId, "Member" + senderId + " has joined the channel.");
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You are not subscribed to this channel.");
        }
    }

    // 강퇴 처리. 클라이언트에 메세지 송신
    private void handleKickRequest(Long channelId, Long senderId, Long targetMemberId) {
        // 권한 확인 아직 구현 안 됨.
        memberChannelSubscriptionService.kickMember(channelId, targetMemberId);
        messagingTemplate.convertAndSend("/topic/chat/" + channelId, "Member " + targetMemberId + " has been kicked from the channel.");
        messagingTemplate.convertAndSend("/topic/errors/" + targetMemberId, "You have been kicked from the channel " + channelId + ".");
    }

    // 메세지 처리, sender가 구독자면 메세지를 보내고 아니면 클라이언트에 오류 메세지 돌려줌.
    private void sendMessage(Long channelId, Long senderId, String content) {
        if (memberChannelSubscriptionService.isSubscriber(channelId, senderId)) {
            MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, content);
            messagingTemplate.convertAndSend("/topic/chat/" + channelId, savedMessageDTO);
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You are not subscribed to this channel.");
        }
    }
}

