package webprogrammingTeam.matchingService.domain.matching.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webprogrammingTeam.matchingService.domain.matching.repository.MatchingRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.program.entity.Program;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;

    public void matchingProgramAndMember(Program program, Member member){
        matchingRepository.save(program, member);
    }

    public List<Program> findAllProgramByMemberId(Long memberId){
        return matchingRepository.findAllProgramByMemberId(memberId);
    }

    public List<Member> findAllProgramByProgramId(Long programId)
    {
        return matchingRepository.findAllMemberByProgramId(programId);
    }

}
