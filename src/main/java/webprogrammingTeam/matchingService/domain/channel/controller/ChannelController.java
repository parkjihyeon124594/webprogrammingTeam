package webprogrammingTeam.matchingService.domain.channel.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.dto.CreateChannelRequest;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.util.List;

@RestController
@RequestMapping("/channel") // throw exception 처리 해야함.
@Tag(name = "채팅 채널", description = "채팅 채널 관련 Api")
public class ChannelController {

    private ChannelService channelService;

    @GetMapping("/titles")
    @Operation(summary = "모든 공개 채널 조회", description = "모든 공개 채널을 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ChannelTitleDTO>>> getAllPublicChannelsTitles() {
        List<ChannelTitleDTO> channelTitles =  channelService.getAllPublicChannelTitles();
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,channelTitles));
    }

    @PostMapping("/public")
    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createPublicChannel(@RequestBody CreateChannelRequest request) {
        Channel publicChannel = channelService.createPublicChannel(request.getTitle());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, publicChannel.getChannelId()));
    }

    @PostMapping("/private")
    @Operation(summary = "비밀 채널 생성", description = "비밀 채널을 생성하는 로직")
    public  ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createPrivateChannel(@RequestBody CreateChannelRequest request) {
        Channel privateChannel = channelService.createPrivateChannel(request.getTitle());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, privateChannel.getChannelId()));
    }

    // 채팅방 id로 채팅방 삭제 (단 실수를 방지하기 위해 연관된 메세지와 구독이 존재하면 삭제 거부 예외 구현해야함)
    @DeleteMapping("/{channelId}")
    @Operation(summary = "채널 삭제", description = "채널을 삭제하는 로직, 관련 메세지와 구독 삭제 필요")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteChat(@PathVariable Long channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

}
