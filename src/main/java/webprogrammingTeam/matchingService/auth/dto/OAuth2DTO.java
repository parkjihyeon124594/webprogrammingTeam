package webprogrammingTeam.matchingService.auth.dto;


import lombok.Builder;
import webprogrammingTeam.matchingService.domain.member.entity.Role;
import webprogrammingTeam.matchingService.domain.member.entity.Member;

import java.util.Map;

@Builder
public record OAuth2DTO(Map<String,Object> attributes,String name,String email,String role) {

    public Member oAuth2DtoToMember(OAuth2DTO oAuth2DTO) {
        return Member.builder()
                .memberName(oAuth2DTO.name())
                .email(oAuth2DTO.email())
                .role(Role.valueOf(oAuth2DTO.role()))
                .build();
    }

}
