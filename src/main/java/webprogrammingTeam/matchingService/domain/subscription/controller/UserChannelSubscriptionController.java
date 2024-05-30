package webprogrammingTeam.matchingService.domain.subscription.controller;

import webprogrammingTeam.matchingService.domain.subscription.dto.ChannelDTO;
import webprogrammingTeam.matchingService.domain.subscription.dto.AddChannelAndSubscriptionRequest;
import webprogrammingTeam.matchingService.domain.subscription.dto.AddSubscriptionRequest;
import webprogrammingTeam.matchingService.domain.subscription.dto.UserChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.service.UserChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user-channel-subscription")
public class UserChannelSubscriptionController {

    private final UserChannelSubscriptionService userChannelSubscriptionService;

    @Autowired
    public UserChannelSubscriptionController(UserChannelSubscriptionService userChannelSubscriptionService) {
        this.userChannelSubscriptionService = userChannelSubscriptionService;
    }

    // 모든 구독 반환
    @GetMapping
    public ResponseEntity<List<UserChannelSubscriptionDTO>> getAllSubscription() {
        List<UserChannelSubscriptionDTO> allSubscription =  userChannelSubscriptionService.getAllSubscription();
        //DTO로 바꾸기
        return ResponseEntity.ok().body(allSubscription);
    }

    // 같은 유저 id를 가진 구독들을 반환
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getChatIdsByUserId(@PathVariable Long userId) {
        List<Long> chatIds = userChannelSubscriptionService.findChatIdsByUserId(userId);
        return ResponseEntity.ok().body(chatIds);
    }

    // 같은 채팅채널 id를 가진 구독들을 반환
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Long>> getUserIdsByChannelId(@PathVariable Long channelId) {
        List<Long> userIds = userChannelSubscriptionService.findUserIdsByChannelId(channelId);
        return ResponseEntity.ok().body(userIds);
    }

    // 유저 id와 새로운 채팅채널 제목으로, 새로운 채팅채널 만들고 구독하기 -> 채팅채널 dto, 구독 dto 반환
    @PostMapping("/channel")
    public ResponseEntity<ChannelDTO> createChannelWithSubscription(@RequestBody AddChannelAndSubscriptionRequest request) throws IOException {
        ChannelDTO channelDTO = userChannelSubscriptionService.createChannelWithSubscription(request.getUserId(), request.getChannelTitle());
        return ResponseEntity.ok().body(channelDTO);
    }

    // 유저 id와 채팅채널 id로, 이미 있는 채팅채널에 구독 하기 -> 구독 dto 반환
    @PostMapping
    public void createSubscription(@RequestBody AddSubscriptionRequest request) throws IOException {
        userChannelSubscriptionService.createSubscription(request.getUserId(), request.getChannelId());
    }

    // 구독 id로 구독 삭제
    @DeleteMapping("{subscriptionId}")
    public void deleteSubscription(@PathVariable Long subscriptionId) {
        userChannelSubscriptionService.deleteSubscription(subscriptionId);
    }

    // 같은 유저 id를 가진 구독들을 삭제
    @DeleteMapping("/user/{userId}")
    public void deleteSubscriptionByUserId(@PathVariable Long userId) {
        userChannelSubscriptionService.deleteSubscriptionByUserId(userId);
    }

    // 같은 채팅채널 id를 가진 구독들을 삭제
    @DeleteMapping("/channel/{channelId}")
    public void deleteSubscriptionByChannelId(@PathVariable Long channelId) {
        userChannelSubscriptionService.deleteSubscriptionByChannelId(channelId);
    }
}
