package webprogrammingTeam.matchingService.domain.matching.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import webprogrammingTeam.matchingService.domain.matching.repository.MatchingRepository;
import webprogrammingTeam.matchingService.domain.member.dto.response.MemberIdReadResponse;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.review.dto.request.ReviewSaveRequest;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingRepository matchingRepository;


    /////dto를 어떻게 하면 좋을까
    @GetMapping("/{programId}")
    @Operation(summary = "프로그램의 모든 참여자 리스트", description = "특정 프로그램의 모든 참여자 리스트 얻는  로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<MemberIdReadResponse>> getAllMemberByProgram(
            @PathVariable("programId") Long programId
    ) throws IOException {
        matchingRepository.findAllMemberByProgramId(programId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }
}
