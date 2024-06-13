package webprogrammingTeam.matchingService.domain.geometry.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.geometry.dto.response.GeometryResponse;
import webprogrammingTeam.matchingService.domain.geometry.service.GeometryService;
import webprogrammingTeam.matchingService.domain.member.dto.request.MemberCreateRequest;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.exception.MemberErrorCode;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.global.exception.GlobalException;
import webprogrammingTeam.matchingService.global.util.ApiUtil;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/geometry")
@Slf4j
public class GeometryController {

    private final GeometryService geometryService;
    private final MemberRepository memberRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<GeometryResponse>>> GemetryProgram(
            @AuthenticationPrincipal PrincipalDetails principalDetails
            )
    {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(()->new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Program> programs = geometryService.findProgramsNearMember(member.getLatitude(),member.getLongitude());

        List<GeometryResponse> geometryResponses = geometryService.programToGeometryResponse(programs);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,geometryResponses));

    }


}
