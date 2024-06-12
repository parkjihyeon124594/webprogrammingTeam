package webprogrammingTeam.matchingService.domain.message.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Autowired
    public SubscriptionInterceptor(MemberChannelSubscriptionService memberChannelSubscriptionService) {
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (isSubscribeCommandToPrivateChannel(accessor)) {
            PrincipalDetails principal = (PrincipalDetails) accessor.getUser();
            if (principal != null) {
                Long memberId = principal.getMember().getId();
                Long channelId = extractChannelIdFromDestination(accessor.getDestination());

                if (!memberChannelSubscriptionService.isSubscriber(channelId, memberId)) {
                    throw new AccessDeniedException("You are not allowed to subscribe to this private channel.");
                }
            }
        }

        return message;
    }

    private boolean isSubscribeCommandToPrivateChannel(StompHeaderAccessor accessor) {
        return accessor.getCommand() == StompCommand.SUBSCRIBE &&
                accessor.getDestination() != null &&
                accessor.getDestination().startsWith("/topic/chat/private/");
    }

    private Long extractChannelIdFromDestination(String destination) {
        // 경로에서 채널 ID 추출
        // 경로 형식이 /topic/chat/{channelType}/{channelId}라고 가정합니다.
        String[] parts = destination.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}

