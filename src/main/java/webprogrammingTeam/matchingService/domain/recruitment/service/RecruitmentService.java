package webprogrammingTeam.matchingService.domain.recruitment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webprogrammingTeam.matchingService.domain.notification.service.NotificationService;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.recruitment.dto.MemberProgramRecruitmentResponse;
import webprogrammingTeam.matchingService.domain.recruitment.dto.ProgramRecruitmentResponse;

import webprogrammingTeam.matchingService.domain.recruitment.dto.RecruitmentRequest;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;
import webprogrammingTeam.matchingService.domain.recruitment.repository.RecruitmentRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;
    private final NotificationService notificationService;

    //사용자가 프로그램에 지원하는 로직
    @Transactional
    public Long recruitmentProgram(Long programId, String email) throws AccessDeniedException {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Program program = programRepository.findById(programId).get();

        if(!member.getEmail().equals(email)){
            throw new AccessDeniedException("본인만 프로그램을 신청할 수 있습니다");
        }

        if(program.getMember().getEmail().equals(email)){
            throw new AccessDeniedException("개최자는 본인 프로그램을 신청할 수 없습니다.");
        }

        if(recruitmentRepository.findByProgramIdAndMemberId(program.getId(), member.getId()) != null){
            throw new AccessDeniedException("신청 하셨던 프로그램입니다.");
        }
        if(!program.getOpen().equals(Open.OPEN))
        {
            throw new IllegalStateException("프로그램 정원이 초과되었습니다.");
        }

        Recruitment recruitment = Recruitment.builder()
                .member(member)
                .program(program)
                .build();

        recruitmentRepository.save(recruitment);
        log.info("program.getMaximum(): {}",program.getMaximum());
        log.info("recruitmentRepository.countByProgramId(programId)),{}",recruitmentRepository.countByProgramId(programId));
        program.increaseRecruitment();
        if(program.getMaximum() == program.getRecruitment())
        {
            program.updateOpen(Open.CLOSED);
        }

        String content = "프로그램 신청이 완료되었습니다.: " + program.getTitle();
        String url = "http://localhost:8080/program/view/" + program.getId();
        notificationService.send(member, content, url);

        return recruitment.getId();
    }

    //지원 취소
    public void recruitmentCancel(Long programId, String email) throws AccessDeniedException {

        Member member = memberRepository.findByEmail(email).get();
        Recruitment recruitment = recruitmentRepository.findByProgramIdAndMemberId(programId, member.getId());
        if( recruitment ==null){
            throw new IllegalStateException("프로그램을 신청하지 않았습니다.");
        }
        //지원자 목록에서 삭제하기
        recruitmentRepository.deleteById(recruitment.getId());


        //모집중 변경
        Program program = programRepository.findById(programId).orElseThrow();

        program.decreaseRecruitment();

        log.info("program을 신청한 신청자 수 : {}",recruitmentRepository.countByProgramId(programId));
        if(recruitmentRepository.countByProgramId(programId)< program.getMaximum()){
            program.updateOpen(Open.OPEN);
        }

        programRepository.save(program);
        String content = "프로그램 신청이 취소되었습니다.: " + program.getTitle();
        String url = "http://localhost:8080/program/view/" + program.getId();
        notificationService.send(member, content, url);

        log.info("모집중 변경 open {}",program.getOpen());
    }



    //프로그램의 지원자 리스트
    public List<ProgramRecruitmentResponse> findAllMemberByProgramId(Long programId, String email) throws AccessDeniedException {

        //프로그램 개최자만 확인할 수 있음.
        //프로그램 있는지 확인해주세용..........
        Program program = programRepository.findById(programId).get();
        if(!program.getMember().getEmail().equals(email))
        {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        try{

            List<Member> memberList = recruitmentRepository.findAllMemberByProgramId(programId);
            List<ProgramRecruitmentResponse> responseList = new ArrayList<>();

            for(Member member : memberList){
                responseList.add(
                        new ProgramRecruitmentResponse(member.getMemberName(), member.getEmail(), member.getAge(), member.getGender())
                );
            }
            log.info("responseList 지원자 리스트 {}", responseList);
            return responseList;
        }catch(Exception e){
        }
        return null;

    }

    //사용자가 지원한 프로그램 리스트
    public List<MemberProgramRecruitmentResponse> findAllProgramByMemberEmail(String email)throws IOException
    {
        Member member = memberRepository.findByEmail(email).get();
        List<Program> programList2 = recruitmentRepository.findAllProgramByMemberId(member.getId());
        log.info("내가 지원한 프로그램들: {}",programList2);
        try{
           List<Program> programList = recruitmentRepository.findAllProgramByMemberId(member.getId());

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
