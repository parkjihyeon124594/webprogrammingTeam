package webprogrammingTeam.matchingService.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.recruitment.dto.MemberProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.ProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.RecruitmentRequest;
import webprogrammingTeam.matchingService.domain.recruitment.service.RecruitmentService;
import webprogrammingTeam.matchingService.global.exception.GlobalException;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/program/recruitment")
@Slf4j
@Tag(name = "프로그램 지원", description = "프로그램 지원에 관련 Api")

public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final MemberRepository memberRepository;

    @PostMapping("/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "사용자의 프로그램 신청", description = "사용자가 프로그램을 신청하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> Recruitment(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        Long recruitmentId = recruitmentService.recruitmentProgram(programId, principalDetails.getEmail());        String email = principalDetails.getEmail();

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,recruitmentId));
    }

    @PostMapping("/cancel/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "사용자의 프로그램 신청 취소", description = "사용자가 프로그램을 신청을 취소하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> RecruitmentCancel(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        recruitmentService.recruitmentCancel(programId, principalDetails.getEmail());


        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
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

    @GetMapping("/programs")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "사용자가 지원한 프로그램 리스트", description = "특정 회원의 지원한 프로그램 리스트 얻는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MemberProgramRecruitmentResponse>>> getAllProgramByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        List<MemberProgramRecruitmentResponse> programList = recruitmentService.findAllProgramByMemberEmail(principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programList));
    }

    /* Admin?
    @GetMapping("/programs/{memberId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "한 멤버가 지원한 프로그램 리스트", description = "특정 회원의 지원한 프로그램 리스트 얻는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MemberProgramRecruitmentResponse>>> getAllProgramByMember(
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        List<MemberProgramRecruitmentResponse> programList = recruitmentService.findAllProgramByMemberId(memberId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programList));
    } */


}
