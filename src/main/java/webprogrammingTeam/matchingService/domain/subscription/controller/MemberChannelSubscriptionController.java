package webprogrammingTeam.matchingService.domain.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import webprogrammingTeam.matchingService.domain.subscription.dto.ChannelDTO;
import webprogrammingTeam.matchingService.domain.subscription.dto.AddChannelAndSubscriptionRequest;
import webprogrammingTeam.matchingService.domain.subscription.dto.AddSubscriptionRequest;
import webprogrammingTeam.matchingService.domain.subscription.dto.MemberChannelSubscriptionDTO;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/member-channel-subscription")
@Tag(name = "유저-채널 관계", description = "유저와 채널의 관계를 저장하고 처리하는 Api")
public class MemberChannelSubscriptionController {

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Autowired
    public MemberChannelSubscriptionController(MemberChannelSubscriptionService memberChannelSubscriptionService) {
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
    }

    // 모든 구독 반환
    @GetMapping
    @Operation(summary = "", description = "")
    public ResponseEntity<List<MemberChannelSubscriptionDTO>> getAllSubscription() {
        List<MemberChannelSubscriptionDTO> allSubscription =  memberChannelSubscriptionService.getAllSubscription();
        //DTO로 바꾸기
        return ResponseEntity.ok().body(allSubscription);
    }

    // 같은 유저 id를 가진 구독들을 반환
    @GetMapping("/member/{memberId}")
    @Operation(summary = "", description = "")
    public ResponseEntity<List<Long>> getChatIdsByMemberId(@PathVariable Long memberId) {
        List<Long> chatIds = memberChannelSubscriptionService.findChatIdsByMemberId(memberId);
        return ResponseEntity.ok().body(chatIds);
    }

    // 같은 채팅채널 id를 가진 구독들을 반환
    @GetMapping("/channel/{channelId}")
    @Operation(summary = "", description = "")
    public ResponseEntity<List<Long>> getMemberIdsByChannelId(@PathVariable Long channelId) {
        List<Long> memberIds = memberChannelSubscriptionService.findMemberIdsByChannelId(channelId);
        return ResponseEntity.ok().body(memberIds);
    }

    // 유저 id와 새로운 채팅채널 제목으로, 새로운 채팅채널 만들고 구독하기 -> 채팅채널 dto, 구독 dto 반환
    @PostMapping("/channel")
    @Operation(summary = "", description = "")
    public ResponseEntity<ChannelDTO> createChannelWithSubscription(@RequestBody AddChannelAndSubscriptionRequest request) throws IOException {
        ChannelDTO channelDTO = memberChannelSubscriptionService.createChannelWithSubscription(request.getMemberId(), request.getChannelTitle());
        return ResponseEntity.ok().body(channelDTO);
    }

    // 유저 id와 채팅채널 id로, 이미 있는 채팅채널에 구독 하기 -> 구독 dto 반환
    @PostMapping
    @Operation(summary = "", description = "")
    public void createSubscription(@RequestBody AddSubscriptionRequest request) throws IOException {
        memberChannelSubscriptionService.createSubscription(request.getMemberId(), request.getChannelId());
    }

    // 구독 id로 구독 삭제
    @DeleteMapping("{subscriptionId}")
    @Operation(summary = "", description = "")
    public void deleteSubscription(@PathVariable Long subscriptionId) {
        memberChannelSubscriptionService.deleteSubscription(subscriptionId);
    }

    // 같은 유저 id를 가진 구독들을 삭제
    @DeleteMapping("/member/{memberId}")
    @Operation(summary = "", description = "")
    public void deleteSubscriptionByMemberId(@PathVariable Long memberId) {
        memberChannelSubscriptionService.deleteSubscriptionByMemberId(memberId);
    }

    // 같은 채팅채널 id를 가진 구독들을 삭제
    @DeleteMapping("/channel/{channelId}")
    @Operation(summary = "", description = "")
    public void deleteSubscriptionByChannelId(@PathVariable Long channelId) {
        memberChannelSubscriptionService.deleteSubscriptionByChannelId(channelId);
    }
}
