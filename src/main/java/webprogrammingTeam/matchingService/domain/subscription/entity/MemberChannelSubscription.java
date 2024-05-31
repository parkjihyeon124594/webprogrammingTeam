package webprogrammingTeam.matchingService.domain.subscription.entity;

import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberChannelSubscription {
    // 유저와 채팅방 사이의 다대다 관계

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "channel")
    private Channel channel;

}
