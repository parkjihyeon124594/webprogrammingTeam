package webprogrammingTeam.matchingService.domain.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;


import java.util.List;


public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findAllByMemberId(Long id);

    // 1.특정 카테고리에 속하는 프로그램 검색
    @Query(value = "SELECT p FROM Program p WHERE p.category =:category")
    List<Program> findByCategory(@Param(value= "category") Category category);


    // 2. 작성일자를 기준으로 최신순 프로그램 조회
    @Query(value ="SELECT p FROM Program p ORDER BY 'createDate' DESC")
    List<Program> findByCreateDateOrderByDesc();

    // 3.현재 진행 중인 프로그램만 조회
    @Query(value ="SELECT p FROM Program p WHERE p.open ='OPEN'")
    List<Program> findByOpenIsOpen();

    // 4.사용자 반경 5km의 프로그램 찾기
    List<Program> findByLatitudeBetweenAndLongitudeBetween(Double minLat, Double maxLat, Double minLon, Double maxLon);



}

