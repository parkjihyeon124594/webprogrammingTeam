package webprogrammingTeam.matchingService.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.recruitment.entity.Recruitment;

import java.util.List;


public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {


   // List<Member> findAllMemberByProgramId(Long programId);

    @Query("SELECT r.member FROM Recruitment r WHERE r.program.id = :programId")
    List<Member> findAllMemberByProgramId(@Param("programId") Long programId);

    @Query("SELECT r.program FROM Recruitment r WHERE r.member.id = :memberId")
    List<Program> findAllProgramByMemberId(@Param("memberId") Long memberId);

    Recruitment findByProgramIdAndMemberId(Long programId, Long memberId);

    Long countByProgramId(Long programId);

//   void save(Program program, Member member);
//    void deleteAllByMemberId(Long memberId);
}
