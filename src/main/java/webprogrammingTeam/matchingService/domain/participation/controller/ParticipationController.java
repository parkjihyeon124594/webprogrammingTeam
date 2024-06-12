package webprogrammingTeam.matchingService.domain.participation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @Operation(summary = "프로그램의 모든 참여자 리스트", description = "특정 프로그램의 모든 참여자 리스트 얻는  로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramParticipationResponse>>> getAllMemberByProgram(
            @PathVariable("programId") Long programId
    ) throws IOException {
        List<ProgramParticipationResponse> memberList = participationService.findAllMemberByProgramId(programId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,memberList));
    }

    @GetMapping("/programs/{memberId}")
    @Operation(summary = "한 멤버가 참여한 프로그램 리스트", description = "특정 회원의 참여한 모든 프로그램 리스트 얻는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MemberProgramHistoryResponse>>> getAllProgramByMember(
            @PathVariable("memberId") Long memberId
    ) throws IOException {
        List<MemberProgramHistoryResponse> programList = participationService.findAllProgramByMemberId(memberId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programList));
    }



}
