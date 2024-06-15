package webprogrammingTeam.matchingService.domain.message.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionInterceptorTest {

    @Mock
    private MemberChannelSubscriptionService memberChannelSubscriptionService;

    @InjectMocks
    private SubscriptionInterceptor subscriptionInterceptor;

    @Mock
    private WebSocketSession session;

    private WebSocketHandler webSocketHandler;

    @BeforeEach
    void setUp() {
        webSocketHandler = subscriptionInterceptor.decorate(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                // Do nothing
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                // Do nothing
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                // Do nothing
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                // Do nothing
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        });
    }

    @Test
    @WithMockUser
    void testSubscriptionToPrivateChannelDeniedForNonSubscriber() throws Exception {
        // Given
        String destination = "/topic/chat/private/1";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(mockPrincipal(1L));
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(memberChannelSubscriptionService.isSubscriber(1L, 1L)).thenReturn(false);

        // When / Then
        assertThrows(AccessDeniedException.class, () -> {
            webSocketHandler.handleMessage(session, new TextMessage((byte[]) message.getPayload()));
        });

        verify(memberChannelSubscriptionService, times(1)).isSubscriber(1L, 1L);
    }

    @Test
    @WithMockUser
    void testSubscriptionToPrivateChannelAllowedForSubscriber() throws Exception {
        // Given
        String destination = "/topic/chat/private/1";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(mockPrincipal(1L));
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(memberChannelSubscriptionService.isSubscriber(1L, 1L)).thenReturn(true);

        // When / Then
        assertDoesNotThrow(() -> {
            webSocketHandler.handleMessage(session, new TextMessage((byte[]) message.getPayload()));
        });

        verify(memberChannelSubscriptionService, times(1)).isSubscriber(1L, 1L);
    }

    private Principal mockPrincipal(Long memberId) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(memberId);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getMember()).thenReturn(member);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        return new Principal() {
            @Override
            public String getName() {
                return memberId.toString();
            }
        };
    }
}

