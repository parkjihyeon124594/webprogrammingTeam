package webprogrammingTeam.matchingService.auth.dto;


import lombok.Builder;
import webprogrammingTeam.matchingService.domain.user.entity.Role;
import webprogrammingTeam.matchingService.domain.user.entity.User;

import java.util.Map;

@Builder
public record OAuth2DTO(Map<String,Object> attributes,String name,String email,String role) {


    public User oAuth2DtoToMember(OAuth2DTO oAuth2DTO) {
        return User.builder()
                .userName(oAuth2DTO.name())
                .email(oAuth2DTO.email())
                .role(Role.valueOf(oAuth2DTO.role()))
                .build();
    }

}
