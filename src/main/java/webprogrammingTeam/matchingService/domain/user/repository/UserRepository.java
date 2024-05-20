package webprogrammingTeam.matchingService.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogrammingTeam.matchingService.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByEmail(String email);
}
