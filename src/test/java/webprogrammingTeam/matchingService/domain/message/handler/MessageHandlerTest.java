package webprogrammingTeam.matchingService.domain.message.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.dto.PrivateMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.dto.PublicMessagePayLoad;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import webprogrammingTeam.matchingService.global.exception.GlobalException;
import webprogrammingTeam.matchingService.jwt.JWTService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private MessageHandler messageHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandlePublicMessage_Success() {
        Long channelId = 1L;
        PublicMessagePayLoad payload = new PublicMessagePayLoad("Test Content", "accessToken");

        String senderEmail = "test@example.com";
        Member member = createMember(senderEmail, 1L);

        mockJwtAndMemberRepository(payload.Accesstoken(), senderEmail, member);

        MessageDTO messageDTO = createMessageDTO("Test Content");
        when(messageService.addMessage(channelId, member.getId(), payload.content())).thenReturn(messageDTO);

        messageHandler.handlePublicMessage(channelId, payload);

        verifyInteractionsForHandleMessage(payload.Accesstoken(), senderEmail, channelId, member, messageDTO, "/topic/chat/public/");
    }

    @Test
    public void testHandlePublicMessage_MemberNotFound() {
        Long channelId = 1L;
        PublicMessagePayLoad payload = new PublicMessagePayLoad("accessToken", "Test Content");

        String senderEmail = "test@example.com";

        when(jwtService.getEmail(payload.Accesstoken())).thenReturn(senderEmail);
        when(memberRepository.findByEmail(senderEmail)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> messageHandler.handlePublicMessage(channelId, payload));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND.code(), exception.getCode());
        verify(jwtService).getEmail(payload.Accesstoken());
        verify(memberRepository).findByEmail(senderEmail);
        verifyNoInteractionsForHandleMessage();
    }

    @Test
    public void testHandlePrivateMessage_Success() {
        Long channelId = 1L;
        PrivateMessagePayLoad payload = new PrivateMessagePayLoad("Test Content", "accessToken");

        String senderEmail = "test@example.com";
        Member member = createMember(senderEmail, 1L);

        mockJwtAndMemberRepository(payload.Accesstoken(), senderEmail, member);

        MessageDTO messageDTO = createMessageDTO("Test Content");
        when(messageService.addMessage(channelId, member.getId(), payload.content())).thenReturn(messageDTO);

        messageHandler.handlePrivateMessage(channelId, payload);

        verifyInteractionsForHandleMessage(payload.Accesstoken(), senderEmail, channelId, member, messageDTO, "/topic/chat/private/");
    }

    @Test
    public void testHandlePrivateMessage_MemberNotFound() {
        Long channelId = 1L;
        PrivateMessagePayLoad payload = new PrivateMessagePayLoad("accessToken", "Test Content");

        String senderEmail = "test@example.com";

        when(jwtService.getEmail(payload.Accesstoken())).thenReturn(senderEmail);
        when(memberRepository.findByEmail(senderEmail)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> messageHandler.handlePrivateMessage(channelId, payload));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND.code(), exception.getCode());
        verify(jwtService).getEmail(payload.Accesstoken());
        verify(memberRepository).findByEmail(senderEmail);
        verifyNoInteractionsForHandleMessage();
    }

    private Member createMember(String email, Long id) {
        Member member = Member.builder()
                .email(email)
                .build();
        member.setIdForTest(id);
        return member;
    }

    private MessageDTO createMessageDTO(String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent(content);
        return messageDTO;
    }

    private void mockJwtAndMemberRepository(String accessToken, String senderEmail, Member member) {
        when(jwtService.getEmail(accessToken)).thenReturn(senderEmail);
        when(memberRepository.findByEmail(senderEmail)).thenReturn(Optional.of(member));
    }

    private void verifyInteractionsForHandleMessage(String accessToken, String senderEmail, Long channelId, Member member, MessageDTO messageDTO, String topicPrefix) {
        verify(jwtService).getEmail(accessToken);
        verify(memberRepository).findByEmail(senderEmail);
        verify(messageService).addMessage(channelId, member.getId(), messageDTO.getContent());
        verify(messagingTemplate).convertAndSend(topicPrefix + channelId, messageDTO);
    }

    private void verifyNoInteractionsForHandleMessage() {
        verify(messageService, never()).addMessage(anyLong(), anyLong(), anyString());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(MessageDTO.class));
    }
}
