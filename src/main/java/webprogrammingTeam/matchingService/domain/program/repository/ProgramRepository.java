package webprogrammingTeam.matchingService.domain.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.program.entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
