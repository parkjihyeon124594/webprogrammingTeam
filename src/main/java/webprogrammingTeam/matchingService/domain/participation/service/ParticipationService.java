package webprogrammingTeam.matchingService.domain.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.participation.dto.ParticipationRequest;
import webprogrammingTeam.matchingService.domain.participation.dto.ProgramParticipationResponse;
import webprogrammingTeam.matchingService.domain.participation.dto.MemberProgramHistoryResponse;
import webprogrammingTeam.matchingService.domain.participation.entity.Participation;
import webprogrammingTeam.matchingService.domain.participation.repository.ParticipationRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;

    //선착순으로 모집완료 되게 해야함

//    @Transactional
//    public Long matchingProgramAndMember(ParticipationRequest participationRequest){
//        Member member = memberRepository.findById(participationRequest.memberId()).get();
//        Program program = programRepository.findById(participationRequest.programId()).get();
//
//        Participation participation= Participation.builder()
//                .member(member)
//                .program(program)
//                .build();
//        participationRepository.save(participation);
//
//        return participation.getId();
//    }

    public void participationFirstCome(Member member, Program program){



    }

    //프로그램 참여자 리스트
    public List<ProgramParticipationResponse> findAllMemberByProgramId(Long programId, String email)throws IOException {

        Program program = programRepository.findById(programId).orElseThrow();
        if(!program.getMember().getEmail().equals(email))
        {
            throw new AccessDeniedException("개최자만 리스트를 확인할 수 있습니다.");
        }
        try{
            List<Member> memberList = participationRepository.findAllMemberByProgramId(programId);

            List<ProgramParticipationResponse> responseList = new ArrayList<>();

            for(Member member : memberList){
                responseList.add(
                        new ProgramParticipationResponse(member.getMemberName(), member.getEmail(), member.getBirth(), member.getGender())
                );
            }

            return responseList;
        }catch(Exception e){
        }
        return null;

    }

    public List<MemberProgramHistoryResponse> findAllProgramByMemberId(Long memberId, String email)throws IOException
    {
        Member member = memberRepository.findById(memberId).orElseThrow();
        if(!member.getEmail().equals(email))
        {
            throw new AccessDeniedException("본인만 참여 리스트를 확인할 수 있습니다.");
        }
        try{
            List<Program> programList = participationRepository.findAllProgramByMemberId(memberId);

            List<MemberProgramHistoryResponse> responseList = new ArrayList<>();

            for(Program program : programList){
                responseList.add(
                        new MemberProgramHistoryResponse(program.getId(), program.getTitle(), program.getCategory(), program.getProgramDate())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;
    }

}
