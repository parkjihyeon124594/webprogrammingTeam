package webprogrammingTeam.matchingService.domain.message.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

class SubscriptionInterceptorTest {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionInterceptorTest.class);

    @Mock
    private MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Mock
    private WebSocketSession session;

    @Mock
    private WebSocketHandler webSocketHandler;

    @InjectMocks
    private SubscriptionInterceptor subscriptionInterceptor;

    private WebSocketHandlerDecorator handlerDecorator;
    private PrincipalDetails principalDetails;
    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handlerDecorator = (WebSocketHandlerDecorator) subscriptionInterceptor.decorate(webSocketHandler);

        // Member와 PrincipalDetails 초기화
        member = Member.builder()
                .id(1L)
                .memberName("Test Member")
                .email("test@example.com")
                .birth("1990-01-01")
                .gender("Male")
                .password("password")
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        principalDetails = new PrincipalDetails(member);
    }

    @Test
    void testHandleMessage_ValidSubscription() throws Exception {
        // Given
        Long memberId = 1L;
        Long channelId = 2L;
        String destination = "/topic/chat/private/2";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(principalDetails);

        // 헤더를 포함한 내부 메시지 생성
        accessor.setSessionId(session.getId());
        accessor.setLeaveMutable(true);
        Message<byte[]> internalMessage = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());


        doReturn(true).when(memberChannelSubscriptionService).isSubscriber(channelId, memberId);

        // When
        handlerDecorator.handleMessage(session, new TextMessage(new String(internalMessage.getPayload(), StandardCharsets.UTF_8)));

        // Then
        verify(memberChannelSubscriptionService, times(1)).isSubscriber(channelId, memberId);
        verify(session, never()).sendMessage(any());
        verify(session, never()).close();
    }

    @Test
    void testHandleMessage_InvalidSubscription() throws Exception {
        // Given
        Long memberId = 1L;
        Long channelId = 2L;
        String destination = "/topic/chat/private/2";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(principalDetails);

        // 헤더를 포함한 내부 메시지 생성
        Message<byte[]> internalMessage = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(memberChannelSubscriptionService.isSubscriber(channelId, memberId)).thenReturn(false);

        // When
        handlerDecorator.handleMessage(session, new TextMessage(new String(internalMessage.getPayload(), StandardCharsets.UTF_8)));

        // Then
        verify(memberChannelSubscriptionService, times(1)).isSubscriber(channelId, memberId);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
        verify(session, times(1)).close();
    }

    @Test
    void testHandleMessage_InvalidUser() throws Exception {
        // Given
        String destination = "/topic/chat/private/2";

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);

        // 헤더를 포함한 내부 메시지 생성
        Message<byte[]> internalMessage = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        // When
        handlerDecorator.handleMessage(session, new TextMessage(new String(internalMessage.getPayload(), StandardCharsets.UTF_8)));

        // Then
        verify(session, times(1)).sendMessage(any(TextMessage.class));
        verify(session, times(1)).close();
    }
}
