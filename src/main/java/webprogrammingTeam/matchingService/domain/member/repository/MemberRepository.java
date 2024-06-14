package webprogrammingTeam.matchingService.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);


    boolean existsByEmail(String email);
}
