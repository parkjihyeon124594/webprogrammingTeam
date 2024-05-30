package webprogrammingTeam.matchingService.domain.channel.controller;

import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channel")
public class ChannelController {

    private ChannelService channelService;

    // 모든 채팅방 반환
    @GetMapping("/titles")
    public ResponseEntity<List<ChannelTitleDTO>> getAllChannelsTitles() {
        List<ChannelTitleDTO> channelTitles =  channelService.getAllChannelTitles();
        return ResponseEntity.ok().body(channelTitles);
    }

    // post 없음. 구독을 자동으로 해야하므로 subscription으로 채팅 생성을 옮김. 게시물에서 채팅을 생성하려면 subscriptionService 써야함.

    // 채팅방 id로 채팅방 삭제 (단 실수를 방지하기 위해 연관된 메세지와 구독이 존재하면 삭제 거부)
    @DeleteMapping("/{chatId}")
    public void deleteChat(@PathVariable Long chatId) {
        channelService.deleteChannel(chatId);
    }

}
