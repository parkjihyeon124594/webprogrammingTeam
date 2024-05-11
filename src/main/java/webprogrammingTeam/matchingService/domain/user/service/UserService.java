package webprogrammingTeam.matchingService.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.auth.dto.OAuth2DTO;
import webprogrammingTeam.matchingService.domain.user.entity.Role;
import webprogrammingTeam.matchingService.domain.user.entity.User;
import webprogrammingTeam.matchingService.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public User saveUser(OAuth2DTO oAuth2DTO){
        User user=oAuth2DTO.oAuth2DtoToMember(oAuth2DTO);
        return userRepository.save(user);
    }
}
