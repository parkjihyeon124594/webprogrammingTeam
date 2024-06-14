package webprogrammingTeam.matchingService.domain.member.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.member.entity.Role;

@Builder
public record MemberIdReadResponse(String memberName, Role role){
}
