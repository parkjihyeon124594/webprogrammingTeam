package webprogrammingTeam.matchingService.domain.mail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.mail.dto.request.EmailCheckRequestDto;
import webprogrammingTeam.matchingService.domain.mail.dto.request.EmailRequestDto;
import webprogrammingTeam.matchingService.domain.mail.dto.response.EmailCheckResponseDto;
import webprogrammingTeam.matchingService.domain.mail.service.EmailService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;
import webprogrammingTeam.matchingService.global.util.RedisUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Slf4j
public class EmailController {

    private final EmailService emailService;
    private final RedisUtil redisUtil;

    @PostMapping("/mailsend")
    public ResponseEntity<ApiUtil.ApiSuccessResult<String>> mailSend(
            @RequestBody EmailRequestDto emailRequestDto){
      log.info("이메일 인증 요청 : {}", emailRequestDto.email());
      String email = emailRequestDto.email();
      String authNum = emailService.joinEmail(email);
      redisUtil.setDataExpire(authNum,email,300);

      return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, emailRequestDto.email()));
    }

    @PostMapping("/mailAuthCheck")
    public ResponseEntity<ApiUtil.ApiSuccessResult<EmailCheckResponseDto>> mailAuthCheck(
            @RequestBody EmailCheckRequestDto emailCheckRequestDto
            ){
        Boolean Checked=emailService.CheckAuthNum(emailCheckRequestDto.email(), emailCheckRequestDto.authNum());
        String message =null;
        if(Checked){
            message="이메일 인증 완료";
        }
        else{
            message="이메일 인증 실패";
        }
        EmailCheckResponseDto emailCheckResponseDto= EmailCheckResponseDto.builder()
                .message(message).build();

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,emailCheckResponseDto));
    }
}


