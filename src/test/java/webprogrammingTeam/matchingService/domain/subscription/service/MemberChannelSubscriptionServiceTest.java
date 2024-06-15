package webprogrammingTeam.matchingService.domain.subscription.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import webprogrammingTeam.matchingService.domain.subscription.dto.MemberChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.entity.MemberChannelSubscription;
import webprogrammingTeam.matchingService.domain.subscription.repository.MemberChannelSubscriptionRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberChannelSubscriptionServiceTest {

    @Mock
    private MemberChannelSubscriptionRepository memberChannelSubscriptionRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private MemberChannelSubscriptionService memberChannelSubscriptionService;

    private Member member;
    private Channel channel;
    private MemberChannelSubscription subscription;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .memberName("Test Member")
                .build();

        channel = new Channel();
        channel.setChannelId(1L);
        channel.setTitle("Test Channel");
        channel.setPublic(true);

        subscription = new MemberChannelSubscription();
        subscription.setMember(member);
        subscription.setChannel(channel);
    }

    @Test
    void testGetAllSubscription() {
        when(memberChannelSubscriptionRepository.findAll()).thenReturn(Arrays.asList(subscription));

        List<MemberChannelSubscriptionDTO> result = memberChannelSubscriptionService.getAllSubscription();
        assertEquals(1, result.size());
        assertEquals("Test Member", result.get(0).getMemberName());
    }

    @Test
    void testFindChatIdsByMemberId() {
        when(memberChannelSubscriptionRepository.findByMember_Id(1L)).thenReturn(Arrays.asList(subscription));

        List<Long> result = memberChannelSubscriptionService.findChatIdsByMemberId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    void testFindMemberNamesByChannelId() {
        when(memberChannelSubscriptionRepository.findByChannel_ChannelId(1L)).thenReturn(Arrays.asList(subscription));

        List<String> result = memberChannelSubscriptionService.findMemberNamesByChannelId(1L);
        assertEquals(1, result.size());
        assertEquals("Test Member", result.get(0));
    }

    @Test
    void testCreatePrivateChannelAndSubscription() throws IOException {
        when(channelService.createPrivateChannel("Test Channel")).thenReturn(channel);
        when(memberService.getMemberById(1L)).thenReturn(member);
        when(memberChannelSubscriptionRepository.save(any(MemberChannelSubscription.class))).thenReturn(subscription);

        Long channelId = memberChannelSubscriptionService.createPrivateChannelAndSubscription("Test Channel", Arrays.asList(1L));
        assertEquals(1L, channelId);
    }

    @Test
    void testCreateSubscription() throws IOException {
        // Mock 객체 설정
        when(memberService.getMemberById(1L)).thenReturn(member);
        when(channelService.getChannelById(1L)).thenReturn(channel);
        when(memberChannelSubscriptionRepository.save(any(MemberChannelSubscription.class)))
                .thenAnswer(invocation -> {
                    MemberChannelSubscription subscription = invocation.getArgument(0);
                    subscription.setSubscriptionId(1L); // subscriptionId 설정
                    return subscription;
                });

        // 메서드 호출 및 검증
        Long subscriptionId = memberChannelSubscriptionService.createSubscription(1L, 1L);
        System.out.printf(String.valueOf(subscriptionId));
        assertNotNull(subscriptionId);
        assertEquals(Long.valueOf(1L), subscriptionId);
    }

    @Test
    void testDeleteSubscription() {
        when(memberChannelSubscriptionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberChannelSubscriptionRepository).deleteById(1L);

        memberChannelSubscriptionService.deleteSubscription(1L);

        verify(memberChannelSubscriptionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSubscription_NotExist() {
        when(memberChannelSubscriptionRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> memberChannelSubscriptionService.deleteSubscription(1L));
    }

    @Test
    void testDeleteSubscriptionByMemberId() {
        doNothing().when(memberChannelSubscriptionRepository).deleteByMember_Id(1L);

        memberChannelSubscriptionService.deleteSubscriptionByMemberId(1L);

        verify(memberChannelSubscriptionRepository, times(1)).deleteByMember_Id(1L);
    }

    @Test
    void testDeleteSubscriptionByChannelId() {
        doNothing().when(memberChannelSubscriptionRepository).deleteByChannel_ChannelId(1L);

        memberChannelSubscriptionService.deleteSubscriptionByChannelId(1L);

        verify(memberChannelSubscriptionRepository, times(1)).deleteByChannel_ChannelId(1L);
    }

    @Test
    void testIsSubscriber() {
        when(channelService.getChannelById(1L)).thenReturn(channel);
        when(memberService.getMemberById(1L)).thenReturn(member);
        when(memberChannelSubscriptionRepository.existsByChannelAndMember(channel, member)).thenReturn(true);

        boolean isSubscriber = memberChannelSubscriptionService.isSubscriber(1L, 1L);
        assertTrue(isSubscriber);
    }
}
