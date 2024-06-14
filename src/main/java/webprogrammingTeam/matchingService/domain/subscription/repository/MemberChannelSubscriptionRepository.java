package webprogrammingTeam.matchingService.domain.subscription.repository;

import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.subscription.entity.MemberChannelSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberChannelSubscriptionRepository extends JpaRepository<MemberChannelSubscription, Long>  {

    boolean existsByChannelAndMember(Channel channel, Member memberId);

    List<MemberChannelSubscription> findByMember_Id(Long memberId);

    List<MemberChannelSubscription> findByChannel_ChannelId(Long channelId);

    void deleteByMember_Id(Long memberId);

    void deleteByChannel_ChannelId(Long channelId);

    void deleteByChannelAndMember(Channel channel, Member member);
}
