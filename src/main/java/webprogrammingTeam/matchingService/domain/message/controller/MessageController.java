package webprogrammingTeam.matchingService.domain.message.controller;

import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.entity.Message;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 같은 채팅채널 id를 가진 메세지들을 반환
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesByChannelId(@PathVariable Long channelId) {
        List<MessageDTO> allMessages = messageService.findAllMessageByChannelId(channelId);
        return ResponseEntity.ok().body(allMessages);
    }

    // post 없음. message handler가 메세지 송신과 수신을 처리함.

    // 메세지 id로 메세지 삭제
    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.delete(messageId);
    }

    // 같은 채팅채널 id를 가진 메세지들을 삭제
    @DeleteMapping("/channel/{channelId}")
    public void deleteAllMessagesByChannelId(@PathVariable Long channelId) {
        messageService.deleteAllMessageByChannelId(channelId);
    }
}
