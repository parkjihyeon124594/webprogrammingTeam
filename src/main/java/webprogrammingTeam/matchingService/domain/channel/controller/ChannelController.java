package webprogrammingTeam.matchingService.domain.channel.controller;

import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.dto.CreateChannelRequest;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channel") // throw exception 처리 해야함.
public class ChannelController {

    private ChannelService channelService;

    // 모든 채팅방 반환
    @GetMapping("/titles")
    public ResponseEntity<List<ChannelTitleDTO>> getAllChannelsTitles() {
        List<ChannelTitleDTO> channelTitles =  channelService.getAllPublicChannelTitles();
        return ResponseEntity.ok().body(channelTitles);
    }
    @PostMapping("/public")
    public ResponseEntity<Long> createPublicChannel(@RequestBody CreateChannelRequest request) {
        Channel publicChannel = channelService.createPublicChannel(request.getTitle());
        return ResponseEntity.ok().body(publicChannel.getChannelId());
    }

    @PostMapping("/private")
    public ResponseEntity<Long> createPrivateChannel(@RequestBody CreateChannelRequest request) {
        Channel privateChannel = channelService.createPrivateChannel(request.getTitle());
        return ResponseEntity.ok().body(privateChannel.getChannelId());
    }
    // 채팅방 id로 채팅방 삭제 (단 실수를 방지하기 위해 연관된 메세지와 구독이 존재하면 삭제 거부 예외 구현해야함)
    @DeleteMapping("/{channelId}")
    public void deleteChat(@PathVariable Long channelId) {
        channelService.deleteChannel(channelId);
    }

}
