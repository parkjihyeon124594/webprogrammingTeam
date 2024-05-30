package webprogrammingTeam.matchingService.domain.subscription.repository;

import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.subscription.entity.UserChannelSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChannelSubscriptionRepository extends JpaRepository<UserChannelSubscription, Long>  {

    boolean existsByChannelAndUser(Channel channel, User userId);

    List<UserChannelSubscription> findByUser_UserId(Long userId);

    List<UserChannelSubscription> findByChannel_ChannelId(Long channelId);

    void deleteByUser_UserId(Long userId);

    void deleteByChannel_ChannelId(Long channelId);

    void deleteByChannelAndUser(Channel channel, User user);
}
