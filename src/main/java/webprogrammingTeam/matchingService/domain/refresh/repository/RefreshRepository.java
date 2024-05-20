package webprogrammingTeam.matchingService.domain.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.refresh.entity.RefreshEntity;

import java.util.Date;

public interface RefreshRepository extends JpaRepository<RefreshEntity,Long> {

    Boolean existsByRefresh(String refresh);

    String findEmailByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
