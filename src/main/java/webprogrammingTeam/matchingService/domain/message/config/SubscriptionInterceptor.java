/*
package webprogrammingTeam.matchingService.domain.message.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SubscriptionInterceptor implements WebSocketHandlerDecoratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionInterceptor.class);
    private static final String PRIVATE_CHANNEL_PREFIX = "/topic/chat/private/";

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
                if (message instanceof org.springframework.web.socket.TextMessage) {
                    org.springframework.web.socket.TextMessage textMessage = (org.springframework.web.socket.TextMessage) message;
                    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(
                            MessageBuilder.withPayload(textMessage.getPayload()).build()
                    );


                    if (isSubscribeCommandToPrivateChannel(accessor)) {
                        Object user = accessor.getUser();
                        if (user instanceof PrincipalDetails) {
                            PrincipalDetails principal = (PrincipalDetails) user;
                            Long memberId = principal.getMember().getId();
                            Long channelId = extractChannelIdFromDestination(accessor.getDestination());

                            logger.debug("Checking subscription for memberId: " + memberId + " and channelId: " + channelId);
                            if (!memberChannelSubscriptionService.isSubscriber(channelId, memberId)) {
                                sendErrorMessage(session, "You are not allowed to subscribe to this private channel.");
                                return;
                            }
                        } else {
                            logger.error("User is not an instance of PrincipalDetails.");
                            sendErrorMessage(session, "Authentication error.");
                            return;
                        }
                    }
                }

                super.handleMessage(session, message);
            }

            private boolean isSubscribeCommandToPrivateChannel(StompHeaderAccessor accessor) {
                return accessor.getCommand() == StompCommand.SUBSCRIBE &&
                        accessor.getDestination() != null &&
                        accessor.getDestination().startsWith(PRIVATE_CHANNEL_PREFIX);
            }

            private Long extractChannelIdFromDestination(String destination) {
                String[] parts = destination.split("/");
                try {
                    return Long.parseLong(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    logger.error("Invalid channel ID format in destination: " + destination);
                    throw e;
                }
            }

            private void sendErrorMessage(WebSocketSession session, String errorMessage) {
                try {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(errorMessage));
                    session.close();
                } catch (IOException e) {
                    logger.error("Error sending error message: " + errorMessage, e);
                }
            }
        };
    }
}
*/
