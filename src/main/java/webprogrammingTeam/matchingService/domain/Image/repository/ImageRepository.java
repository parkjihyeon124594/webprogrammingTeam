package webprogrammingTeam.matchingService.domain.Image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {


    @Query(value = "SELECT * FROM image WHERE program_id = :programId ORDER BY image_id", nativeQuery = true)
    Image findFirstImageByProgram(@Param("programId") Long programId);


    List<Image> findByProgram(Optional<Program> program);
    List<Image> findByProgramId(Long id);

    void deleteAllByProgramId(Long id);
}
