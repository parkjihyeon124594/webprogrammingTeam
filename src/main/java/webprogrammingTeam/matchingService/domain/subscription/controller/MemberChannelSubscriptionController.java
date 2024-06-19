package webprogrammingTeam.matchingService.domain.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.subscription.dto.AddSubscriptionRequest;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/member-channel-subscription")
@Tag(name = "구독(유저-채널 관계)", description = "유저와 채널의 관계를 저장하고 처리하는 Api. 공개 채팅은 저장하지 않고 참여자 채팅만 관계 저장함.")
@Slf4j
public class MemberChannelSubscriptionController {

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Autowired
    public MemberChannelSubscriptionController(MemberChannelSubscriptionService memberChannelSubscriptionService) {
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
    }

    @GetMapping("/member")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "한 유저의 참여 채팅방 조회", description = "유저의 토큰으로 참여한 채널을 조회하는 기능")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<Long>>> getChannelIdsByMemberId(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getId();
        log.info("memberId {} ", memberId);
        List<Long> channelIds = memberChannelSubscriptionService.findChatIdsByMemberId(principalDetails);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, channelIds));
    }

    @GetMapping("/channel/{channelId}")
    @Operation(summary = "한 참여자 채팅에 참여한 모든 유저 이름 조회", description = "채널의 id로 참여한 유저의 이름을 조회하는 기능")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<String>>> getMemberIdsByChannelId(@PathVariable Long channelId) {
        List<String> memberNames = memberChannelSubscriptionService.findMemberNamesByChannelId(channelId);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, memberNames));
    }

    /*@PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "공개 채팅방 구독", description = "토큰과 channel id로 이미 있는 공개 채팅방에 참여하는 기능.")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createPublicChannelSubscription(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody AddSubscriptionRequest request) throws IOException {
        Long channelId = request.getChannelId();
        Long memberId = principalDetails.getMember().getId();

        log.info("memberId {} ", memberId);
        Long subscriptionId = memberChannelSubscriptionService.createSubscription(memberId, channelId);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, subscriptionId));
    }

    @DeleteMapping("{subscriptionId}")
    @Operation(summary = "구독 id로 구독 삭제", description = "구독 id로 하나의 구독을 삭제하는 기능")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteSubscription(@PathVariable Long subscriptionId) {
        memberChannelSubscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

    @DeleteMapping("/member/{memberId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "한 유저의 모든 구독 관계 삭제", description = "유저 id로 연관된 채널-유저 구독을 삭제하는 기능")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteSubscriptionByMemberId(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberChannelSubscriptionService.deleteSubscriptionByMemberId(principalDetails);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

    @DeleteMapping("/channel/{channelId}")
    @Operation(summary = "한 채널의 모든 구독 관계 삭제", description = "채널 id로 연관된 채널-유저 구독을 삭제하는 기능")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteSubscriptionByChannelId(@PathVariable Long channelId) {
        memberChannelSubscriptionService.deleteSubscriptionByChannelId(channelId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }*/
}
