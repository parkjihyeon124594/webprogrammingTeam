package webprogrammingTeam.matchingService.domain.message.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.dto.PrivateMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.dto.PublicMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @Mock
    private MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageHandler messageHandler;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageHandler).build();
    }

    @Test
    @WithMockUser
    void handlePublicMessage_Subscriber() {
        // Given
        Long channelId = 1L;
        Long senderId = 1L;
        String content = "Test public message";
        PublicMessagePayLoad payload = new PublicMessagePayLoad();
        payload.setContent(content);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getMember().getId()).thenReturn(senderId);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChannelId(channelId);
        messageDTO.setContent(content);

        when(memberChannelSubscriptionService.isSubscriber(channelId, senderId)).thenReturn(true);
        when(messageService.addMessage(channelId, senderId, content)).thenReturn(messageDTO);

        // When
        messageHandler.handlePublicMessage(channelId, payload, principalDetails);

        // Then
        verify(messageService, times(1)).addMessage(channelId, senderId, content);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/chat/public/" + channelId, messageDTO);
    }

    @Test
    @WithMockUser
    void handlePublicMessage_NotSubscriber() {
        // Given
        Long channelId = 1L;
        Long senderId = 1L;
        String content = "Test public message";
        PublicMessagePayLoad payload = new PublicMessagePayLoad();
        payload.setContent(content);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getMember().getId()).thenReturn(senderId);

        when(memberChannelSubscriptionService.isSubscriber(channelId, senderId)).thenReturn(false);

        // When
        messageHandler.handlePublicMessage(channelId, payload, principalDetails);

        // Then
        verify(messageService, times(0)).addMessage(any(), any(), any());
        verify(messagingTemplate, times(1)).convertAndSend("/topic/errors/" + senderId, "You have to subscribe from this channel.");
    }

    @Test
    @WithMockUser
    void handlePrivateMessage() {
        // Given
        Long channelId = 1L;
        Long senderId = 1L;
        String content = "Test private message";
        PrivateMessagePayLoad payload = new PrivateMessagePayLoad();
        payload.setContent(content);

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getMember().getId()).thenReturn(senderId);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChannelId(channelId);
        messageDTO.setContent(content);

        when(messageService.addMessage(channelId, senderId, content)).thenReturn(messageDTO);

        // When
        messageHandler.handlePrivateMessage(channelId, payload, principalDetails);

        // Then
        verify(messageService, times(1)).addMessage(channelId, senderId, content);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/chat/private/" + channelId, messageDTO);
    }
}
