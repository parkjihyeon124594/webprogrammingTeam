package webprogrammingTeam.matchingService.domain.participation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.participation.dto.ProgramParticipationResponse;
import webprogrammingTeam.matchingService.domain.participation.dto.MemberProgramHistoryResponse;
import webprogrammingTeam.matchingService.domain.participation.service.ParticipationService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {
    private final ParticipationService participationService;

    @GetMapping("/members/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "프로그램의 모든 참여자 리스트", description = "특정 프로그램의 모든 참여자 리스트 얻는  로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramParticipationResponse>>> getAllMemberByProgram(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
            ) throws IOException {
        List<ProgramParticipationResponse> memberList = participationService.findAllMemberByProgramId(programId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,memberList));
    }

    @GetMapping("/programs/{memberId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "한 멤버가 참여한 프로그램 리스트", description = "특정 회원의 참여한 모든 프로그램 리스트 얻는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MemberProgramHistoryResponse>>> getAllProgramByMember(
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        List<MemberProgramHistoryResponse> programList = participationService.findAllProgramByMemberId(memberId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programList));
    }



}
