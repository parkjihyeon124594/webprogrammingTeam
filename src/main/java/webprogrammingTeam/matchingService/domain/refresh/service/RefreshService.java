package webprogrammingTeam.matchingService.domain.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.refresh.entity.RefreshEntity;
import webprogrammingTeam.matchingService.domain.refresh.repository.RefreshtokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshtokenRepository refreshtokenRepository;
    @Transactional
    public void saveRefreshEntity(String email, String refresh,String role) {

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .email(email)
                .refresh(refresh)
                .role(role)
                .build();

        refreshtokenRepository.save(refreshEntity);
    }

}
