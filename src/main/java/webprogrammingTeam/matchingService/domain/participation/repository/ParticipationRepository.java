package webprogrammingTeam.matchingService.domain.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.participation.entity.Participation;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.program.entity.Program;

import java.util.List;

/*이 인터페이스는 도메인 레이어에 속함. 비즈니스 로직을 정의하고,
 이를 통해 서비스나 컨트롤러 등 다른 부분에서 사용할 수 있는 메서드를 선언합니다.*/
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Member> findAllMemberByProgramId(Long programId);

    List<Program> findAllProgramByMemberId(Long memberId);

    Participation findByProgramIdAndMemberId(Long programId, Long memberId );
//    void deleteAllByMemberId(Long memberId);
}
