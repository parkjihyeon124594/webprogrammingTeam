package webprogrammingTeam.matchingService.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.auth.dto.OAuth2DTO;
import webprogrammingTeam.matchingService.domain.Board.Dto.Response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;
import webprogrammingTeam.matchingService.domain.user.Dto.Response.UserIdReadResponse;
import webprogrammingTeam.matchingService.domain.user.entity.Role;
import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public User saveUser(OAuth2DTO oAuth2DTO){
        User user=oAuth2DTO.oAuth2DtoToMember(oAuth2DTO);
        return userRepository.save(user);
    }

    public UserIdReadResponse findOneUser(Long id) throws IOException{
        User user = userRepository.findById(id)
                .orElseThrow();

        return UserIdReadResponse.builder()
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }



}
