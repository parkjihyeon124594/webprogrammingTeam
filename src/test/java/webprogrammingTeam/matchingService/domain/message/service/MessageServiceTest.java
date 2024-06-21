package webprogrammingTeam.matchingService.domain.message.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.entity.Message;
import webprogrammingTeam.matchingService.domain.message.repository.MessageRepository;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelService channelService;

    @Mock
    private MemberService memberService;

    @Mock
    private MemberChannelSubscriptionService memberChannelSubscriptionService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllMessageByPublicChannelId() {
        Long channelId = 1L;
        Message message = createMessage(1L, "Test Message");

        when(messageRepository.getAllMessagesByChannel_ChannelId(channelId)).thenReturn(Collections.singletonList(message));

        List<MessageDTO> result = messageService.findAllMessageByPublicChannelId(channelId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getContent());
        verify(messageRepository).getAllMessagesByChannel_ChannelId(channelId);
    }

    @Test
    public void testFindAllMessageByPrivateChannelId_Success() {
        Long channelId = 1L;
        Member member = createMember(1L);
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Message message = createMessage(1L, "Test Message");

        when(memberChannelSubscriptionService.isSubscriber(channelId, member.getId())).thenReturn(true);
        when(messageRepository.getAllMessagesByChannel_ChannelId(channelId)).thenReturn(Collections.singletonList(message));

        List<MessageDTO> result = messageService.findAllMessageByPrivateChannelId(channelId, principalDetails);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getContent());
        verify(memberChannelSubscriptionService).isSubscriber(channelId, member.getId());
        verify(messageRepository).getAllMessagesByChannel_ChannelId(channelId);
    }

    @Test
    public void testFindAllMessageByPrivateChannelId_NotSubscriber() {
        Long channelId = 1L;
        Member member = createMember(1L);
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        when(memberChannelSubscriptionService.isSubscriber(channelId, member.getId())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                messageService.findAllMessageByPrivateChannelId(channelId, principalDetails));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Member is not a participant in the specified channel", exception.getReason());
        verify(memberChannelSubscriptionService).isSubscriber(channelId, member.getId());
        verify(messageRepository, never()).getAllMessagesByChannel_ChannelId(channelId);
    }

    @Test
    public void testAddMessage() {
        Long channelId = 1L;
        Long senderId = 1L;
        String content = "Test Message";
        Channel channel = createChannel(channelId);
        Member member = createMemberWithDetails("test@example.com");

        when(channelService.getChannelById(channelId)).thenReturn(channel);
        when(memberService.getMemberById(senderId)).thenReturn(member);

        Message message = createMessageWithDetails(1L, content, channel, member);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        MessageDTO result = messageService.addMessage(channelId, senderId, content);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals("test@example.com", result.getSenderEmail());
        verify(channelService).getChannelById(channelId);
        verify(memberService).getMemberById(senderId);
        verify(messageRepository).save(any(Message.class));
        verify(messageRepository).findById(1L);
    }

    @Test
    public void testDeleteAllMessageByChannelId() {
        Long channelId = 1L;

        messageService.deleteAllMessageByChannelId(channelId);

        verify(messageRepository).deleteByChannel_ChannelId(channelId);
    }

    @Test
    public void testDelete() {
        Long messageId = 1L;

        messageService.delete(messageId);

        verify(messageRepository).deleteById(messageId);
    }

    private Message createMessage(Long messageId, String content) {
        Message message = new Message();
        message.setMessageId(messageId);
        message.setContent(content);

        Channel channel = new Channel();
        channel.setChannelId(1L);

        Member member = Member.builder()
                .email("test@example.com")
                .build();

        message.setSender(member);
        message.setChannel(channel);
        message.setCreateDate(LocalDateTime.now());
        return message;
    }

    private Member createMember(Long memberId) {
        Member member = Member.builder().build();
        member.setIdForTest(memberId);
        return member;
    }

    private Channel createChannel(Long channelId) {
        Channel channel = new Channel();
        channel.setChannelId(channelId);
        return channel;
    }

    private Member createMemberWithDetails(String email) {
        return Member.builder()
                .email(email)
                .build();
    }

    private Message createMessageWithDetails(Long messageId, String content, Channel channel, Member member) {
        Message message = new Message();
        message.setMessageId(messageId);
        message.setContent(content);
        message.setChannel(channel);
        message.setSender(member);
        message.setCreateDate(LocalDateTime.now());
        return message;
    }
}
