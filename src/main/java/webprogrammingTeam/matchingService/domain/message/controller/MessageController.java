package webprogrammingTeam.matchingService.domain.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.util.List;

@RestController
@RequestMapping("/message") // throw exception 처리 해야함.
@Tag(name = "메세지 컨트롤러", description = "채팅 메세지 관련 Api")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping("/public_channel/{channelId}")
    @Operation(summary = "공개 채널의 모든 메세지 조회", description = "공개 채널의 모든 메세지를 조회하는 로직, 채널 입장시 사용")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MessageDTO>>> getAllMessagesByPublicChannelId(@PathVariable("channelId") Long channelId) {
        List<MessageDTO> allMessages = messageService.findAllMessageByPublicChannelId(channelId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allMessages));
    }
    @GetMapping("/private_channel/{channelId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "비밀 채널의 모든 메세지 조회", description = "비밀 채널의 모든 메세지를 조회하는 로직, 채널 입장시 사용")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MessageDTO>>> getAllMessagesByPrivateChannelId(@PathVariable("channelId") Long channelId,
                                                                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MessageDTO> allMessages = messageService.findAllMessageByPrivateChannelId(channelId, principalDetails);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allMessages));
    }

    // post 없음. message handler가 메세지 송신과 수신을 처리함.


    // 메세지 id로 메세지 삭제 (공개, 비공개 나눠야 할 수도 있음)
    // sender 확인 필요
    /*@DeleteMapping("/{messageId}")
    @Operation(summary = "메세지 하나 삭제", description = "하나의 메세지를 삭제하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteMessage(@PathVariable("messageId") Long messageId) {
        messageService.delete(messageId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }

    // 같은 채팅채널 id를 가진 메세지들을 삭제
    @DeleteMapping("/channel/{channelId}")
    @Operation(summary = "채널의 모든 메세지 삭제", description = "하나의 채널에 있는 모든 메세지를 삭제하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteAllMessagesByChannelId(@PathVariable("channelId") Long channelId) {
        messageService.deleteAllMessageByChannelId(channelId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }*/
}
