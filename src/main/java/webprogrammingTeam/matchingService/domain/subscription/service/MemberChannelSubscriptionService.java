package webprogrammingTeam.matchingService.domain.subscription.service;

import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.subscription.dto.ChannelDTO;
import webprogrammingTeam.matchingService.domain.subscription.dto.MemberChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.entity.MemberChannelSubscription;
import webprogrammingTeam.matchingService.domain.subscription.repository.MemberChannelSubscriptionRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberChannelSubscriptionService {

    private final MemberChannelSubscriptionRepository memberChannelSubscriptionRepository;

    private final MemberService memberService;

    private final ChannelService channelService;

    @Autowired
    public MemberChannelSubscriptionService(MemberChannelSubscriptionRepository memberChannelSubscriptionRepository,
                                            MemberService memberService,
                                            ChannelService channelService) {
        this.memberChannelSubscriptionRepository = memberChannelSubscriptionRepository;
        this.memberService = memberService;
        this.channelService = channelService;
    }

    public List<MemberChannelSubscriptionDTO> getAllSubscription() {
        return convertMemberChannelSubscriptionToMemberChannelSubscriptionDTO(memberChannelSubscriptionRepository.findAll());
    }

    private List<MemberChannelSubscriptionDTO> convertMemberChannelSubscriptionToMemberChannelSubscriptionDTO(List<MemberChannelSubscription> subscriptions) {
        return subscriptions.stream()
                .map(this::convertSubscriptionToSubscriptionDTO)
                .collect(Collectors.toList());
    }

    private MemberChannelSubscriptionDTO convertSubscriptionToSubscriptionDTO(MemberChannelSubscription subscription) {
        MemberChannelSubscriptionDTO subscriptionDTO = new MemberChannelSubscriptionDTO();
        subscriptionDTO.setMemberId(subscription.getMember().getId());
        subscriptionDTO.setChannelId(subscription.getChannel().getChannelId());
        return subscriptionDTO;
    }

    public List<Long> findChatIdsByMemberId(Long memberId) {
        return memberChannelSubscriptionRepository.findByMember_Id(memberId)
                .stream()
                .map(subscription -> subscription.getChannel().getChannelId())
                .collect(Collectors.toList());
    }

    public List<Long> findMemberIdsByChannelId(Long channelId) {
        return memberChannelSubscriptionRepository.findByChannel_ChannelId(channelId)
                .stream()
                .map(subscription -> subscription.getMember().getId())
                .collect(Collectors.toList());
    }

    public ChannelDTO createChannelWithSubscription(Long memberId, String channelTitle) throws IOException {
        Member member = memberService.getMemberById(memberId);
        Channel newChannel = channelService.createChannel(channelTitle);

        createSubscription(member.getId(), newChannel.getChannelId());

        ChannelDTO channelDTO = convertChannelToChannelDTO(newChannel);

        return channelDTO;
    }

    public void createSubscription(Long memberId, Long channelId) throws IOException {
        Member member = memberService.getMemberById(memberId);
        Channel channel = channelService.getChannelById(channelId);
        MemberChannelSubscription subscription = new MemberChannelSubscription();
        subscription.setMember(member);
        subscription.setChannel(channel);
        memberChannelSubscriptionRepository.save(subscription);
    }

    private ChannelDTO convertChannelToChannelDTO(Channel channel) {
        ChannelDTO channelDTO = new ChannelDTO();

        channelDTO.setChannelId(channel.getChannelId());
        channelDTO.setTitle(channelDTO.getTitle());

        return channelDTO;
    }

    public void deleteSubscription(Long subscriptionId) {
        if (!memberChannelSubscriptionRepository.existsById(subscriptionId)) {
            throw new IllegalArgumentException("Subscription with ID " + subscriptionId + " does not exist");
        }
        memberChannelSubscriptionRepository.deleteById(subscriptionId);
    }

    public void deleteSubscriptionByMemberId(Long memberId) {
        memberChannelSubscriptionRepository.deleteByMember_Id(memberId);
    }

    public void deleteSubscriptionByChannelId(Long channelId) {
        memberChannelSubscriptionRepository.deleteByChannel_ChannelId(channelId);
    }

    // 고쳐야 함
    public void kickMember(Long channelId, Long targetMemberId) {
        Channel channel = channelService.getChannelById(channelId);

        Member member = memberService.getMemberById(targetMemberId);

        memberChannelSubscriptionRepository.deleteByChannelAndMember(channel, member);
    }

    public boolean isSubscriber(Long channelId, Long senderId) {
        return memberChannelSubscriptionRepository.existsByChannelAndMember(
                channelService.getChannelById(channelId),
                memberService.getMemberById(senderId));
    }


}
