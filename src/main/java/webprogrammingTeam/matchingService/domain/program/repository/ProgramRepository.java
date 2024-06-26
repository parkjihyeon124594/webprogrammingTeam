package webprogrammingTeam.matchingService.domain.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;


import java.time.LocalDate;
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



    @Query("SELECT p FROM Program p WHERE p.recruitmentEndDate < :now AND p.open != 'CLOSED'")
    List<Program> findProgramsToClose(@Param("now") String now);


    // 5.카테고리 + 연령 데이터 집계
    @Query(value = "SELECT SUBSTRING_INDEX(p.program_address, ' ', 1) AS city, " +
            "       p.category, " +
            "       SUM(CASE WHEN m.age BETWEEN 10 AND 19 THEN 1 ELSE 0 END) AS teen, " +
            "       SUM(CASE WHEN m.age BETWEEN 20 AND 29 THEN 1 ELSE 0 END) AS twenties, " +
            "       SUM(CASE WHEN m.age BETWEEN 30 AND 39 THEN 1 ELSE 0 END) AS thirties, " +
            "       SUM(CASE WHEN m.age BETWEEN 40 AND 49 THEN 1 ELSE 0 END) AS forties, " +
            "       SUM(CASE WHEN m.age BETWEEN 50 AND 59 THEN 1 ELSE 0 END) AS fifties, " +
            "       SUM(CASE WHEN m.age BETWEEN 60 AND 69 THEN 1 ELSE 0 END) AS sixties, " +
            "       SUM(CASE WHEN m.age BETWEEN 70 AND 79 THEN 1 ELSE 0 END) AS seventies, " +
            "       SUM(CASE WHEN m.age BETWEEN 80 AND 89 THEN 1 ELSE 0 END) AS eighties " +
            "FROM Program p " +
            "JOIN Recruitment r ON p.program_id = r.program_id " +
            "JOIN Member m ON r.member_id = m.member_id " +
            "GROUP BY city,p.category", nativeQuery = true)
    List<Object[]> findAgeGroupCountsByCategory();

    // 6.월별 프로그램 집계
    @Query(value = "SELECT " +
            "DATE_FORMAT(STR_TO_DATE(p.program_date, '%Y-%m-%d %H:%i'), '%Y-%m') AS month, " +
            "p.category AS category, " +
            "COUNT(*) AS program_count " +
            "FROM Program p " +
            "GROUP BY DATE_FORMAT(STR_TO_DATE(p.program_date, '%Y-%m-%d %H:%i'), '%Y-%m'), p.category " +
            "ORDER BY month, category", nativeQuery = true)
    List<Object[]> findMonthlyCategoryCounts();

}

