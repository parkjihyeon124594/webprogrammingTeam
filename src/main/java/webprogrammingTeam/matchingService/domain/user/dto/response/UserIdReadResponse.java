package webprogrammingTeam.matchingService.domain.user.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.user.entity.Role;
import webprogrammingTeam.matchingService.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Builder
public record UserIdReadResponse(String userName, Role role){
}
