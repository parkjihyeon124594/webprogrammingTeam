package webprogrammingTeam.matchingService.domain.program.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramAllReadResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramIdReadResponse;
import webprogrammingTeam.matchingService.domain.program.service.ProgramService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/program")
@Tag(name = "게시글", description = "게시글 관련 Api")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;
    private final ImageService imageService;



    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 추가", description = "게시글을 추가하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createProgram(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(value="ProgramSaveRequest") ProgramSaveRequest programSaveRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        List<Image> listImage = imageService.saveImageList(images);
        Long saveId = programService.saveProgram(programSaveRequest,listImage,principalDetails.getMember());


        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping()
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회하는 로직, 메인 게시판 용도")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramAllReadResponse>>> getAllProgram(){
        List<ProgramAllReadResponse> allProgram = programService.findAllProgram();
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allProgram));
    }

    @GetMapping("/{programId}")
    @Operation(summary = "게시글 하나 조회", description = "하나의 게시글을 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<ProgramIdReadResponse>> getProgram(
            @PathVariable("programId") Long programId
    )throws IOException{
        ProgramIdReadResponse programIdReadResponse = programService.findOneProgram(programId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, programIdReadResponse));
    }


    @PutMapping("/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 수정", description = "특정 게시글을 수정하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> updateProgram(
            @RequestPart(value="ProgramUpdateRequest") ProgramUpdateRequest programUpdateRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    )throws IOException{
        List<Image> listImage = imageService.saveImageList(images);
        Long updateId = programService.updateProgram(programUpdateRequest, listImage, programId, principalDetails.getMember());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, updateId));
    }

    @DeleteMapping("/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteProgram(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails)throws IOException{
        programService.deleteProgram(programId, principalDetails.getMember());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }


}
