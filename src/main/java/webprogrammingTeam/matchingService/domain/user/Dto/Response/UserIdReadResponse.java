package webprogrammingTeam.matchingService.domain.user.Dto.Response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.user.entity.Role;

@Builder
public record UserIdReadResponse(String userName, Role role){
}
