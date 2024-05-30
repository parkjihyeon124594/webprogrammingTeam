package webprogrammingTeam.matchingService.domain.subscription.service;

import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.subscription.dto.ChannelDTO;
import webprogrammingTeam.matchingService.domain.subscription.dto.UserChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.entity.UserChannelSubscription;
import webprogrammingTeam.matchingService.domain.subscription.repository.UserChannelSubscriptionRepository;
import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserChannelSubscriptionService {

    private final UserChannelSubscriptionRepository userChannelSubscriptionRepository;

    private final UserService userService;

    private final ChannelService channelService;

    @Autowired
    public UserChannelSubscriptionService(UserChannelSubscriptionRepository userChannelSubscriptionRepository,
                                          UserService userService,
                                          ChannelService channelService) {
        this.userChannelSubscriptionRepository = userChannelSubscriptionRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    public List<UserChannelSubscriptionDTO> getAllSubscription() {
        return convertUserChannelSubscriptionToUserChannelSubscriptionDTO(userChannelSubscriptionRepository.findAll());
    }

    private List<UserChannelSubscriptionDTO> convertUserChannelSubscriptionToUserChannelSubscriptionDTO(List<UserChannelSubscription> subscriptions) {
        return subscriptions.stream()
                .map(this::convertSubscriptionToSubscriptionDTO)
                .collect(Collectors.toList());
    }

    private UserChannelSubscriptionDTO convertSubscriptionToSubscriptionDTO(UserChannelSubscription subscription) {
        UserChannelSubscriptionDTO subscriptionDTO = new UserChannelSubscriptionDTO();
        subscriptionDTO.setUserId(subscription.getUser().getId());
        subscriptionDTO.setChannelId(subscription.getChannel().getChannelId());
        return subscriptionDTO;
    }

    public List<Long> findChatIdsByUserId(Long userId) {
        return userChannelSubscriptionRepository.findByUser_UserId(userId)
                .stream()
                .map(subscription -> subscription.getChannel().getChannelId())
                .collect(Collectors.toList());
    }

    public List<Long> findUserIdsByChannelId(Long channelId) {
        return userChannelSubscriptionRepository.findByChannel_ChannelId(channelId)
                .stream()
                .map(subscription -> subscription.getUser().getId())
                .collect(Collectors.toList());
    }

    public ChannelDTO createChannelWithSubscription(Long userId, String channelTitle) throws IOException {
        User user = userService.getUserById(userId);
        Channel newChannel = channelService.createChannel(channelTitle);

        createSubscription(user.getId(), newChannel.getChannelId());

        ChannelDTO channelDTO = convertChannelToChannelDTO(newChannel);

        return channelDTO;
    }

    // 지금 상태로는 user service를 user repository 로 해햐함.
    public void createSubscription(Long userId, Long channelId) throws IOException {
        User user = userService.getUserById(userId);
        Channel channel = channelService.getChannelById(channelId);
        //이렇게 하는 거랑, service에 validation하는 거랑 뭐가 다르지?
        UserChannelSubscription subscription = new UserChannelSubscription();
        subscription.setUser(user);
        subscription.setChannel(channel);
        userChannelSubscriptionRepository.save(subscription);
    }

    private ChannelDTO convertChannelToChannelDTO(Channel channel) {
        ChannelDTO channelDTO = new ChannelDTO();

        channelDTO.setChannelId(channel.getChannelId());
        channelDTO.setTitle(channelDTO.getTitle());

        return channelDTO;
    }

    public void deleteSubscription(Long subscriptionId) {
        if (!userChannelSubscriptionRepository.existsById(subscriptionId)) {
            throw new IllegalArgumentException("Subscription with ID " + subscriptionId + " does not exist");
        }
        userChannelSubscriptionRepository.deleteById(subscriptionId);
    }

    public void deleteSubscriptionByUserId(Long userId) {
        userChannelSubscriptionRepository.deleteByUser_UserId(userId);
    }

    public void deleteSubscriptionByChannelId(Long channelId) {
        userChannelSubscriptionRepository.deleteByChannel_ChannelId(channelId);
    }

    // 고쳐야 함
    public void kickUser(Long channelId, Long targetUserId) {
        Channel channel = channelService.getChannelById(channelId);

        User user = userService.getUserById(targetUserId);

        userChannelSubscriptionRepository.deleteByChannelAndUser(channel, user);
    }

    public boolean isSubscriber(Long channelId, Long senderId) {
        return userChannelSubscriptionRepository.existsByChannelAndUser(
                channelService.getChannelById(channelId),
                userService.getUserById(senderId));
    }


}
