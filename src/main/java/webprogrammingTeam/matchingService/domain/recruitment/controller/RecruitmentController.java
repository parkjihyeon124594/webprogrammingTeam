package webprogrammingTeam.matchingService.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.recruitment.dto.MemberProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.ProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.RecruitmentRequest;
import webprogrammingTeam.matchingService.domain.recruitment.service.RecruitmentService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "사용자의 프로그램 신청", description = "사용자가 프로그램을 신청하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> Recruitment(
            @RequestPart(value="RecruitmentRequest") RecruitmentRequest recruitmentRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        Long recruitmentId = recruitmentService.recruitmentProgram(recruitmentRequest, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,recruitmentId));
    }

    @GetMapping("/members/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "특정 프로그램에 지원한 지원자 리스트", description = "특정 프로그램의 모든 지원자 리스트 얻는  로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramRecruitmentResponse>>> getAllMemberByProgram(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        List<ProgramRecruitmentResponse> memberList = recruitmentService.findAllMemberByProgramId(programId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,memberList));
    }

    @GetMapping("/programs/{memberId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "한 멤버가 지원한 프로그램 리스트", description = "특정 회원의 지원한 프로그램 리스트 얻는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MemberProgramRecruitmentResponse>>> getAllProgramByMember(
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        List<MemberProgramRecruitmentResponse> programList = recruitmentService.findAllProgramByMemberId(memberId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programList));
    }


}