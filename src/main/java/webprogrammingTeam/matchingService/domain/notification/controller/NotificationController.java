package webprogrammingTeam.matchingService.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.notification.service.NotificationService;


import java.io.IOException;

@RestController
@RequestMapping("/notification")
@Tag(name = "알림", description = "알림 관련 Api")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "알림 구독", description = "알림을 구독함.")
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @RequestHeader(value = "LastEventId", required = false, defaultValue = "") String lastEventId)
    {
        return notificationService.subscribe(principalDetails.getEmail(), lastEventId);
    }


}

