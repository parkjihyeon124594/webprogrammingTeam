package webprogrammingTeam.matchingService.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.auth.dto.OAuth2DTO;
import webprogrammingTeam.matchingService.domain.user.dto.response.UserIdReadResponse;
import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.user.repository.UserRepository;

import java.io.IOException;

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
