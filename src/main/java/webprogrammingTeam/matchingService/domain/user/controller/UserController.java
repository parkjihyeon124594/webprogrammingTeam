package webprogrammingTeam.matchingService.domain.user.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import webprogrammingTeam.matchingService.domain.user.dto.response.UserIdReadResponse;
import webprogrammingTeam.matchingService.domain.user.service.UserService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<UserIdReadResponse>> getUser(
            @PathVariable("userId") Long userId
    )throws IOException {

        UserIdReadResponse userIdReadResponse = userService.findOneUser(userId) ;
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, userIdReadResponse));
    }



}
