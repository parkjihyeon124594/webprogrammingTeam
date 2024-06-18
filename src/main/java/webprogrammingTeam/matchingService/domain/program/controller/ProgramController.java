package webprogrammingTeam.matchingService.domain.program.controller;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramAgeDataByCityAndCategoryRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.program.dto.response.*;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.program.service.ProgramService;
import webprogrammingTeam.matchingService.global.rateLimiting.exception.RateLimiterException;
import webprogrammingTeam.matchingService.global.rateLimiting.service.BucketService;
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
    private final BucketService bucketService;


    @PostMapping("/data/city-category-age")
    @Operation(summary = "도시와 카테고리를 파라미터로 받아 연령별 참여율 데이터 조회",description = "연령별 참여율 데이터 조회")
    public ResponseEntity<ApiUtil.ApiSuccessResult<CategoryAgeGroupListResponse>> getProgramAgeDataByCityAndCategory(
            // @RequestPart(value="ProgramSaveRequest") ProgramSaveRequest programSaveRequest,
            @RequestBody ProgramAgeDataByCityAndCategoryRequest programAgeDataByCityAndCategoryRequest
            ){
        CategoryAgeGroupListResponse categoryAgeGroupResponses = programService.findByCityAndCategory(programAgeDataByCityAndCategoryRequest.city(),programAgeDataByCityAndCategoryRequest.category());

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,categoryAgeGroupResponses));

    }

    @GetMapping("/data/category-age")
    @Operation(summary = "카테고리 별로 연령 참여율 데이터 조회", description = "카테고리 별로 연령 조회 ")
    public ResponseEntity<ApiUtil.ApiSuccessResult<CategoryAgeGroupListResponse>> getProgramDataCategoryAge(){
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programService.findParticipantCountsByCategoryAndAgeGruoup()));
    }

    @GetMapping("/data/age-category")
    @Operation(summary = "연령 별로 카테고리 참여율 데이터 조회", description = "연령 별로 카테고리 참여율 데이터 조회")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<Test>>> findParticipantCountsByAgeGroupAndCategory(){
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programService.findParticipantCountsByAgeGroupAndCategory()));
    }

    @GetMapping("/data/monthly-category")
    @Operation(summary = "데이터 조회 ",description = "월별 카테고리 현황 조회")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<MonthlyCategoryCountResponse>>> getProgramDataMonthlyCategory(){

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK,programService.getMonthlyCategoryCounts()));
    }



    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 추가", description = "게시글을 추가하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createProgram(
            HttpServletRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(value="ProgramSaveRequest") ProgramSaveRequest programSaveRequest,
            @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {

        Bucket bucket = bucketService.resolveBucket(request);
        log.info("접근 IP = {}", request.getRemoteAddr());

        if (bucket.tryConsume(1)) { // 1개 사용 요청
            log.info("프로그램 생성됨");
            Long saveId = programService.saveProgram(programSaveRequest,images,principalDetails.getEmail());
            return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
        }


            return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.valueOf(RateLimiterException.TOO_MANY_REQUEST)));
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
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<CategoryProgramReadResponse>>> getCategoryProgram(
            @PathVariable("category") String categoryStr)
    {
        Category category = Category.valueOf(categoryStr);
        List<CategoryProgramReadResponse> categoryProgramReadResponse = programService.programListToProgramCategoryReadResponseList(
                programRepository.findByCategory(category)
        );

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, categoryProgramReadResponse));
    }

    @GetMapping("/category/date")
    @Operation(summary = "최신순 게시글 조회", description = "최신순 게시글 조회하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<CategoryProgramReadResponse>>> getCategoryDateDescProgram(){

        List<Program> dateDescProgram = programRepository.findByCreateDateOrderByDesc();
        List<CategoryProgramReadResponse> categoryProgramReadResponse = programService.programListToProgramCategoryReadResponseList(dateDescProgram);


        log.info("program controller list size {}",dateDescProgram.size());
        log.info("program controller programCategoryReadResponse list size {}", categoryProgramReadResponse.size());


        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, categoryProgramReadResponse));
    }

    @GetMapping("/category/open")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<CategoryProgramReadResponse>>> getCategoryOpenProgram(){
        List<Program> openProgram = programRepository.findByOpenIsOpen();
        List<CategoryProgramReadResponse> categoryProgramReadResponse = programService.programListToProgramCategoryReadResponseList(openProgram);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, categoryProgramReadResponse));
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 쓴 모든 프로그램 조회", description = "내가 쓴 모든 프로그램 조회")
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<ProgramAllReadResponse>>> getMyPrograms(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    )throws IOException{
        List<ProgramAllReadResponse> programAllReadResponse = programService.findAllMyPrograms(principalDetails.getEmail());
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, programAllReadResponse));
    }



    @PutMapping("/{programId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 수정", description = "특정 게시글을 수정하는 로직")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> updateProgram(
            @RequestPart(value="ProgramUpdateRequest") ProgramUpdateRequest programUpdateRequest,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @PathVariable("programId") Long programId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    )throws IOException{
        Long updateId = programService.updateProgram(programUpdateRequest, images, programId, principalDetails.getEmail());
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
