package webprogrammingTeam.matchingService.domain.program.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramCategoryReadResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramIdReadResponse;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.program.service.ProgramService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/program")
@Tag(name = "게시글", description = "게시글 관련 Api")
@RequiredArgsConstructor
@Slf4j
public class ProgramController {
    private final ProgramService programService;
    private final ImageService imageService;
    private final ProgramRepository programRepository;



    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 추가", description = "게시글을 추가하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createProgram(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(value="ProgramSaveRequest") ProgramSaveRequest programSaveRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        List<Image> listImage = imageService.saveImageList(images);
        Long saveId = programService.saveProgram(programSaveRequest,listImage,principalDetails.getEmail());


        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping("/view")
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회하는 로직, 메인 게시판 용도")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramAllReadResponse>>> getAllProgram(){
        List<ProgramAllReadResponse> allProgram = programService.findAllProgram();
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allProgram));
    }

    @GetMapping("/view/{programId}")
    @Operation(summary = "게시글 하나 조회", description = "하나의 게시글을 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<ProgramIdReadResponse>> getProgram(
            @PathVariable("programId") Long programId
    )throws IOException{
        ProgramIdReadResponse programIdReadResponse = programService.findOneProgram(programId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, programIdReadResponse));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리 별 게시글 조회", description = "카테고리 별 게시글 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramCategoryReadResponse>>> getCategoryProgram(
            @PathVariable("category") String categoryStr)
    {
        Category category = Category.valueOf(categoryStr);
        List<ProgramCategoryReadResponse> programCategoryReadResponse = programService.programListToProgramCategoryReadResponseList(
                programRepository.findByCategory(category)
        );

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programCategoryReadResponse));
    }

    @GetMapping("/category/date")
    @Operation(summary = "최신순 게시글 조회", description = "최신순 게시글 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramCategoryReadResponse>>> getCategoryDateDescProgram(){

        List<Program> dateDescProgram = programRepository.findByCreateDateOrderByDesc();
        List<ProgramCategoryReadResponse> programCategoryReadResponse = programService.programListToProgramCategoryReadResponseList(dateDescProgram);


        log.info("program controller list size {}",dateDescProgram.size());
        log.info("program controller programCategoryReadResponse list size {}",programCategoryReadResponse.size());


        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programCategoryReadResponse));
    }

    @GetMapping("/category/open")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramCategoryReadResponse>>> getCategoryOpenProgram(){
        List<Program> openProgram = programRepository.findByOpenIsOpen();
        List<ProgramCategoryReadResponse> programCategoryReadResponse = programService.programListToProgramCategoryReadResponseList(openProgram);


        log.info("program controller list size {}",openProgram.size());
        log.info("program controller programCategoryReadResponse list size {}",programCategoryReadResponse.size());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programCategoryReadResponse));
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
        Long updateId = programService.updateProgram(programUpdateRequest, listImage, programId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, updateId));
    }

    @DeleteMapping("/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteProgram(
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails)throws IOException{
        programService.deleteProgram(programId, principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }



}
