package webprogrammingTeam.matchingService.domain.message.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

@Component
public class SubscriptionInterceptor implements WebSocketHandlerDecoratorFactory {

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Autowired
    public SubscriptionInterceptor(MemberChannelSubscriptionService memberChannelSubscriptionService) {
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap((Message<?>) message);

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

                super.handleMessage(session, message);
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
        };
    }
}
