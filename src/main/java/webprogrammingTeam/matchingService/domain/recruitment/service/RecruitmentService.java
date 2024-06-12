package webprogrammingTeam.matchingService.domain.recruitment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webprogrammingTeam.matchingService.domain.recruitment.dto.MemberProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.ProgramRecruitmentResponse;

import webprogrammingTeam.matchingService.domain.recruitment.dto.RecruitmentRequest;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;
import webprogrammingTeam.matchingService.domain.recruitment.repository.RecruitmentRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public Long recruitmentProgram(RecruitmentRequest recruitmentRequest, Member princialMember) throws AccessDeniedException {
        Member member = memberRepository.findById(recruitmentRequest.memberId()).get();
        Program program = programRepository.findById(recruitmentRequest.programId()).get();

        if(!member.equals(princialMember)){
            throw new AccessDeniedException("본인만 프로그램을 신청할 수 있습니다");
        }

        if(recruitmentRepository.findByProgramIdAndMemberId(program.getId(), member.getId()) != null){
            throw new AccessDeniedException("신청 하셨던 프로그램입니다.");
        }

        Recruitment recruitment = Recruitment.builder()
                .member(member)
                .program(program)
                .build();

        recruitmentRepository.save(recruitment);

        return recruitment.getId();
    }


    //프로그램의 지원자 리스트
    public List<ProgramRecruitmentResponse> findAllMemberByProgramId(Long programId, Member princialMember) throws AccessDeniedException {

        //프로그램 개최자만 확인할 수 있음.
        //프로그램 있는지 확인해주세용..........
        Program program = programRepository.findById(programId).get();
        if(!program.getMember().equals(princialMember))
        {
            throw new AccessDeniedException("신청 하셨던 프로그램입니다.");
        }


        try{
            List<Member> memberList = recruitmentRepository.findAllMemberByProgramId(programId);

            List<ProgramRecruitmentResponse> responseList = new ArrayList<>();

            for(Member member : memberList){
                responseList.add(
                        new ProgramRecruitmentResponse(member.getMemberName(), member.getEmail(), member.getBirth(), member.getGender())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;

    }

    //사용자가 지원한 프로그램 리스트
    public List<MemberProgramRecruitmentResponse> findAllProgramByMemberId(Long memberId)
    {
        try{
            List<Program> programList = recruitmentRepository.findAllProgramByMemberId(memberId);

            List<MemberProgramRecruitmentResponse> responseList = new ArrayList<>();

            for(Program program : programList){
                responseList.add(
                        new MemberProgramRecruitmentResponse(program.getId(), program.getTitle(), program.getCategory(), program.getProgramDate())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;
    }

}
