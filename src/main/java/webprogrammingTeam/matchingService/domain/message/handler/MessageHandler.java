package webprogrammingTeam.matchingService.domain.message.handler;

import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.dto.MessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.domain.subscription.service.UserChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageHandler {
    // session을 관리해야 서버에서 강제로 소켓을 끊을 수 있음. 구독 정보가 없으면 웹소켓을 끊으면 됨. 일단 나중에.
    private final MessageService messageService;
    private final UserChannelSubscriptionService userChannelSubscriptionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageHandler(MessageService messageService,
                          UserChannelSubscriptionService userChannelSubscriptionService,
                          SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userChannelSubscriptionService = userChannelSubscriptionService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{channelId}")
    public void handleMessage(@DestinationVariable Long channelId, @Payload MessagePayLoad messagePayLoad) {
        Long senderId = messagePayLoad.getSenderId();

        if (messagePayLoad.isSubscribeRequest()) {
            handleSubscriptionRequest(channelId, senderId);
        } else if (messagePayLoad.isKickRequest()) {
            handleKickRequest(channelId, senderId, messagePayLoad.getKickUserId());
        } else {
            sendMessage(channelId, senderId, messagePayLoad.getContent());
        }
    }

    // 구독 처리. 구독 정보가 있으면 정상적으로 구독해주고, 아니면 클라이언트에 오류 돌려줌
    private void handleSubscriptionRequest(Long channelId, Long senderId) {
        if (userChannelSubscriptionService.isSubscriber(channelId, senderId)) {
            messagingTemplate.convertAndSend("/topic/chat/" + channelId, "User " + senderId + " has joined the channel.");
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You are not subscribed to this channel.");
        }
    }

    // 강퇴 처리. 클라이언트에 메세지 송신
    private void handleKickRequest(Long channelId, Long senderId, Long targetUserId) {
        // 권한 확인 아직 구현 안 됨.
        /*if (userChannelSubscriptionService.hasKickPermission(channelId, senderId)) { //이거 하려면 권한 추가해야겠네
            userChannelSubscriptionService.kickUser(channelId, targetUserId);
            messagingTemplate.convertAndSend("/topic/chat/" + channelId, "User " + targetUserId + " has been kicked from the channel.");
            messagingTemplate.convertAndSend("/topic/errors/" + targetUserId, "You have been kicked from the channel " + channelId + ".");
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You do not have permission to kick users.");
        }*/
        userChannelSubscriptionService.kickUser(channelId, targetUserId);
        messagingTemplate.convertAndSend("/topic/chat/" + channelId, "User " + targetUserId + " has been kicked from the channel.");
        messagingTemplate.convertAndSend("/topic/errors/" + targetUserId, "You have been kicked from the channel " + channelId + ".");
    }

    // 메세지 처리, sender가 구독자면 메세지를 보내고 아니면 클라이언트에 오류 메세지 돌려줌.
    private void sendMessage(Long channelId, Long senderId, String content) {
        if (userChannelSubscriptionService.isSubscriber(channelId, senderId)) {
            MessageDTO savedMessageDTO = messageService.addMessage(channelId, senderId, content);
            messagingTemplate.convertAndSend("/topic/chat/" + channelId, savedMessageDTO);
        } else {
            messagingTemplate.convertAndSend("/topic/errors/" + senderId, "You are not subscribed to this channel.");
        }
    }
}

