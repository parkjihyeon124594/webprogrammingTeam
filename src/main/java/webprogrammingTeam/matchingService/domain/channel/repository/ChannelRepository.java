package webprogrammingTeam.matchingService.domain.channel.repository;

import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<ChannelTitleDTO> findAllProjectedByIsPublicTrue();
}
