package webprogrammingTeam.matchingService.domain.program.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.program.dto.response.*;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final MemberRepository memberRepository;


    public List<MonthlyCategoryCountResponse> getMonthlyCategoryCounts() {
        List<Object[]> results = programRepository.findMonthlyCategoryCounts();
        // 스트림 연산을 통해 DTO로 변환하여 반환
        return results.stream()
                .map(this::mapToMonthlyCategoryCountDTO)
                .collect(Collectors.toList());
    }
    private MonthlyCategoryCountResponse mapToMonthlyCategoryCountDTO(Object[] result) {
        // 각 Object 배열에서 데이터 추출
        return new MonthlyCategoryCountResponse(
                (String) result[0],    // month
                (String) result[1],    // category
                (Long) result[2]       // programCount
        );
    }
    public CategoryAgeGroupListResponse getAgeGroupCountsByCategory() {
        List<Object[]> rawData = programRepository.findAgeGroupCountsByCategory();
        List<CategoryAgeGroupResponse> ageGroupDTOs = new ArrayList<>();

        for (Object[] row : rawData) {
            String city = (String) row[0];
            String category = (String) row[1];
            int teen = ((Number) row[2]).intValue();
            int twenties = ((Number) row[3]).intValue();
            int thirties = ((Number) row[4]).intValue();
            int forties = ((Number) row[5]).intValue();
            int fifties = ((Number) row[6]).intValue();
            int sixties = ((Number) row[7]).intValue();
            int seventies = ((Number) row[8]).intValue();
            int eighties = ((Number) row[9]).intValue();

            CategoryAgeGroupResponse categoryAgeGroupResponse = CategoryAgeGroupResponse.builder()
                    .city(city)
                    .category(String.valueOf(category))
                    .teen(teen)
                    .twenties(twenties)
                    .thirties(thirties)
                    .forties(forties)
                    .fifties(fifties)
                    .sixties(sixties)
                    .seventies(seventies)
                    .eighties(eighties)
                    .build();

            ageGroupDTOs.add(categoryAgeGroupResponse);
        }

        return new CategoryAgeGroupListResponse(ageGroupDTOs);
    }

    @Transactional
    public Long saveProgram(ProgramSaveRequest programSaveRequest, MultipartFile[] imageList, String email) throws IOException {

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("member 이 없습니다"));

        Program program = Program.builder()
                .member(member)// 글을 쓴 사람이다.
                .title(programSaveRequest.title())
                .content(programSaveRequest.content())
                .category(programSaveRequest.category())
                .maximum(programSaveRequest.maximum())
                .recruitmentStartDate(programSaveRequest.recruitmentStartDate())
                .recruitmentEndDate(programSaveRequest.recruitmentEndDate())
                .programDate(programSaveRequest.programDate())
                .open(programSaveRequest.open())
                .latitude(programSaveRequest.latitude())
                .longitude(programSaveRequest.longitude())
                .recruitment(0)
                .programAddress(programSaveRequest.programAddress())
                .build();

        imageService.uploadImages(program, imageList);

        programRepository.save(program);

        return program.getId();

    }

    public List<ProgramAllReadResponse> findAllProgram() {
         try{
            List<Program> programList = programRepository.findAll();
            log.info("programList{} ", programList);

            List<ProgramAllReadResponse> responseList = new ArrayList<>();

            for(Program program : programList){
                Image image = imageRepository.findFirstImageByProgram(program.getId());
                String imageUrl = image.getUrl();

                responseList.add(
                        new ProgramAllReadResponse(program.getId(), program.getTitle(), program.getCategory(), program.getOpen(), program.getCreateDate(), imageUrl, program.getRecruitment())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;
    }

    public ProgramIdReadResponse findOneProgram(Long id)throws IOException{
        Program program = programRepository.findById(id)
                .orElseThrow();

        List<Image> imageList =  imageService.getImageList(Optional.ofNullable(program));

        List<String> imageUrls = new ArrayList<>();

        for(Image image: imageList){
            imageUrls.add(image.getUrl());
        }

        return ProgramIdReadResponse.builder()
                .programId(program.getId())
                .memberEmail(program.getMember().getEmail())
                .title(program.getTitle())
                .content(program.getContent())
                .category(program.getCategory())
                .maximum(program.getMaximum())
                .recruitmentStartDate(program.getRecruitmentStartDate())
                .recruitmentEndDate(program.getRecruitmentEndDate())
                .programDate(program.getProgramDate())
                .open(program.getOpen())
                .images(imageUrls)
                .recruitment(program.getRecruitment())
                .build();
    }

    public List<ProgramAllReadResponse> findAllMyPrograms(String email) throws IOException{
        try{
            Member member = memberRepository.findByEmail(email).orElseThrow();
            List<Program> programList = programRepository.findAllByMemberId(member.getId());

            List<ProgramAllReadResponse> responseList = new ArrayList<>();

            for(Program program : programList){
                Image image = imageRepository.findFirstImageByProgram(program.getId());
                String imageUrl = image.getUrl();
                responseList.add(
                        new ProgramAllReadResponse(program.getId(), program.getTitle(), program.getCategory(), program.getOpen(), program.getCreateDate(), imageUrl, program.getRecruitment())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;
    }
    @Transactional
    public Long updateProgram(ProgramUpdateRequest programUpdateRequest, MultipartFile[] newImageList, Long programId, String email)throws IOException{

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NoSuchElementException("program이 없습니다"));

        //Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("member 이 없습니다"));
        if(!program.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("program을 수정할 권한이 없습니다.");
        }

        imageRepository.deleteAllByProgramId(programId);

        imageService.uploadImages(program, newImageList);

        program.updateProgram(programUpdateRequest);

        programRepository.save(program);

        return program.getId();
    }


    @Transactional
    public void deleteProgram(Long programId,String email) throws AccessDeniedException {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NoSuchElementException("program이 없습니다"));
        if(!program.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("program을 삭제할 권한이 없습니다.");
        }
        programRepository.delete(program);
    }

    public static String writingTimeToString(LocalDateTime writingTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return writingTime.format(formatter);
    }

    public List<CategoryProgramReadResponse> programListToProgramCategoryReadResponseList(List<Program> programs) {


        return programs.stream()
                .map(program -> new CategoryProgramReadResponse(
                        program.getId(),
                        program.getTitle(),
                        program.getCategory(),
                        program.getOpen(),
                        program.getCreateDate(),
                        imageRepository.findFirstImageByProgram(program.getId()).getUrl()
                                ))
                .collect(Collectors.toList());
    }



}
