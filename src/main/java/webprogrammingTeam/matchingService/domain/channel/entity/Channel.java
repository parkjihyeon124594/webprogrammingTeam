package webprogrammingTeam.matchingService.domain.channel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

@Entity
@Getter
@Setter
public class Channel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id", updatable = false)
    private Long channelId;

    @Column(name = "title")
    private String title;

    @Column(name = "is_public")
    private boolean isPublic;

    // 그냥 board를 가져와서 저장할까?
}