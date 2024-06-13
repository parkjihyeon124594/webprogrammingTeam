package webprogrammingTeam.matchingService.auth.controller;


import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.refresh.exception.RefreshErrorcode;
import webprogrammingTeam.matchingService.domain.refresh.repository.RefreshRepository;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.global.exception.GlobalException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logout")
public class LogoutController {

    private final RefreshRepository refreshRepository;

    @PostMapping
    public ResponseEntity<?> logout(
            @RequestHeader("Refreshtokn") String refreshtokn
    ){

        if(!refreshRepository.existsByRefresh(refreshtokn)){
            throw new GlobalException(RefreshErrorcode.Refresh_NOT_FOUND);
        }

        try {
            refreshRepository.deleteByRefresh(refreshtokn);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }


}
