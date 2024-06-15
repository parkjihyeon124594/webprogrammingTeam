package webprogrammingTeam.matchingService.domain.message.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketConfigTest {

    @Mock
    private SubscriptionInterceptor subscriptionInterceptor;

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @Test
    void testConfigureMessageBroker() {
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        webSocketConfig.configureMessageBroker(registry);

        verify(registry, times(1)).enableSimpleBroker("/topic");
        verify(registry, times(1)).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void testRegisterStompEndpoints() {
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration endpointRegistration = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/ws")).thenReturn(endpointRegistration);

        webSocketConfig.registerStompEndpoints(registry);

        verify(registry, times(1)).addEndpoint("/ws");
        verify(endpointRegistration, times(1)).setAllowedOrigins("*");
    }

    @Test
    void testConfigureWebSocketTransport() {
        WebSocketTransportRegistration registration = mock(WebSocketTransportRegistration.class);

        webSocketConfig.configureWebSocketTransport(registration);

        verify(registration, times(1)).addDecoratorFactory(subscriptionInterceptor);
    }
}

