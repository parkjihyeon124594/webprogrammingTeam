package webprogrammingTeam.matchingService.domain.message.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.entity.Message;
import webprogrammingTeam.matchingService.domain.message.repository.MessageRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.entity.Role;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

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

    private Channel channel;
    private Member member;
    private Message message;

    @BeforeEach
    void setUp() {
        channel = new Channel();
        channel.setChannelId(1L);
        channel.setTitle("Test Channel");
        channel.setPublic(true);

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

        message = new Message();
        message.setMessageId(1L);
        message.setChannel(channel);
        message.setSender(member);
        message.setContent("Test Message");
    }

    @Test
    void testFindAllMessageByPublicChannelId() {
        when(messageRepository.getAllMessagesByChannel_ChannelId(1L)).thenReturn(Arrays.asList(message));

        List<MessageDTO> result = messageService.findAllMessageByPublicChannelId(1L);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getContent());
    }

    @Test
    void testFindAllMessageByPrivateChannelId_Subscriber() {
        when(memberChannelSubscriptionService.isSubscriber(1L, 1L)).thenReturn(true);
        when(messageRepository.getAllMessagesByChannel_ChannelId(1L)).thenReturn(Arrays.asList(message));

        List<MessageDTO> result = messageService.findAllMessageByPrivateChannelId(1L, 1L);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getContent());
    }

    @Test
    void testFindAllMessageByPrivateChannelId_NotSubscriber() {
        when(memberChannelSubscriptionService.isSubscriber(1L, 1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                messageService.findAllMessageByPrivateChannelId(1L, 1L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testAddMessage() {
        when(channelService.getChannelById(1L)).thenReturn(channel);
        when(memberService.getMemberById(1L)).thenReturn(member);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        MessageDTO result = messageService.addMessage(1L, 1L, "Test Message");
        assertNotNull(result);
        assertEquals("Test Message", result.getContent());
    }

    @Test
    void testDeleteAllMessageByChannelId() {
        doNothing().when(messageRepository).deleteByChannel_ChannelId(1L);

        messageService.deleteAllMessageByChannelId(1L);

        verify(messageRepository, times(1)).deleteByChannel_ChannelId(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(messageRepository).deleteById(1L);

        messageService.delete(1L);

        verify(messageRepository, times(1)).deleteById(1L);
    }
}
