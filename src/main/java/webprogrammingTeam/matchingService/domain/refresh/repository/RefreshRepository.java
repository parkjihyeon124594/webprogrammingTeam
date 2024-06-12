package webprogrammingTeam.matchingService.domain.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.refresh.entity.RefreshEntity;


public interface RefreshRepository extends JpaRepository<RefreshEntity,Long> {

    Boolean existsByRefresh(String refresh);


    @Transactional
    void deleteByRefresh(String refresh); // refresh 값이 같은 여러 개의 RefreshEntity 엔티티가 존재한다면, 그 모두가 삭제
}
