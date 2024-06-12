package webprogrammingTeam.matchingService.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;

import java.util.List;


public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {


    List<Member> findAllMemberByProgramId(Long programId);

    List<Program> findAllProgramByMemberId(Long memberId);

    Recruitment findByProgramIdAndMemberId(Long programId, Long memberId);

    Long countByProgramId(Long programId);

//   void save(Program program, Member member);
//    void deleteAllByMemberId(Long memberId);
}
