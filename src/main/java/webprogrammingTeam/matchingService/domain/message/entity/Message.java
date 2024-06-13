package webprogrammingTeam.matchingService.domain.message.entity;

import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

@Entity
@Getter
@Setter
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", updatable = false)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "channel", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "member", nullable = false)
    private Member sender;

    @Column(name = "content")
    private String content;
}
