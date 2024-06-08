package webprogrammingTeam.matchingService.domain.message.controller;

import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message") // throw exception 처리 해야함.
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 같은 채팅채널 id를 가진 메세지들을 반환. isPublic이 ture면 그냥 주고, isPublic이 false면 오류.
    @GetMapping("/public_channel/{channelId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesByPublicChannelId(@PathVariable Long channelId) {
        List<MessageDTO> allMessages = messageService.findAllMessageByPublicChannelId(channelId);
        return ResponseEntity.ok().body(allMessages);
    }

    @GetMapping("/private_channel/{channelId}/member/{memberId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesByPrivateChannelId(@PathVariable Long channelId,
                                                                             @PathVariable Long memberId) {
        List<MessageDTO> allMessages = messageService.findAllMessageByPrivateChannelId(channelId, memberId);
        return ResponseEntity.ok().body(allMessages);
    }

    // post 없음. message handler가 메세지 송신과 수신을 처리함.


    // delete는 member의 권한을 확인해야 함.

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
