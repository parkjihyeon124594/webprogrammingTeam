package webprogrammingTeam.matchingService.domain.program.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramSaveRequest;
import webprogrammingTeam.matchingService.domain.program.dto.request.ProgramUpdateRequest;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramAllReadResponse;
import webprogrammingTeam.matchingService.domain.program.dto.response.ProgramIdReadResponse;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    //private final programService programService;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    LocalDateTime currentTime = LocalDateTime.now();


    //Controller에서 인증된 user 정보를 얻어옴.고쳐야됌.
    Member member;
    @Transactional
    public Long saveProgram(ProgramSaveRequest programSaveRequest, List<Image> imageList){
        Program program = Program.builder()
                .member(member)
                .title(programSaveRequest.title())
                .content(programSaveRequest.content())
                .writingTime(String.valueOf(currentTime))
                .category(programSaveRequest.category())
                .recruitmentStartDate(programSaveRequest.recruitmentStartDate())
                .recruitmentEndDate(programSaveRequest.recruitmentEndDate())
                .programDate(programSaveRequest.programDate())
                .build();
        for(Image i : imageList){
            program.addImageList(i);
        }
        programRepository.save(program);

        return program.getId();

    }

    public List<ProgramAllReadResponse> findAllProgram() {
        try{
            List<Program> programList = programRepository.findAll();

            List<ProgramAllReadResponse> responseList = new ArrayList<>();

            for(Program program : programList){
                responseList.add(
                        new ProgramAllReadResponse(program.getId(), program.getTitle(), program.getCategory(),program.getWritingTime())
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

        List<byte[]> imageByteList = new ArrayList<>();

        for(Image i : imageList){
            byte[] imageData = imageService.downloadImage(i);
            imageByteList.add(imageData);
        }


        return ProgramIdReadResponse.builder()
                .title(program.getTitle())
                .writingTime(program.getWritingTime())
                .content(program.getContent())
                .category(program.getCategory())
                .recruitmentStartDate(program.getRecruitmentStartDate())
                .recruitmentEndDate(program.getRecruitmentEndDate())
                .programDate(program.getProgramDate())
                .imagesByte(imageByteList)
                .build();
    }

    @Transactional
    public Long updateProgram(ProgramUpdateRequest programUpdateRequest, Long programId)throws IOException{
        Program program = programRepository.findById(programId)
                .orElseThrow();

        program.updateProgram(programUpdateRequest);

        return program.getId();
    }

    @Transactional
    public void deleteProgram(Long programId){
        Program program = programRepository.findById(programId)
                .orElseThrow();

        programRepository.delete(program);
    }


}
