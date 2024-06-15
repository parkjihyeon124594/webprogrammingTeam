package webprogrammingTeam.matchingService.domain.refresh.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import webprogrammingTeam.matchingService.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@RedisHash(value="refresh_token")
public class RefreshEntity {
    @Id
    private Long id;
    private String email;
    @Indexed
    private String refresh;
    private String role;

    @TimeToLive
    private long ttl=86400; //24시간

    @Builder
    public RefreshEntity(String email,String refresh,String role){
        this.email=email;
        this.refresh=refresh;
        this.role = role;
    }

}
