package webprogrammingTeam.matchingService.domain.message.repository;


import webprogrammingTeam.matchingService.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> getAllMessagesByChannel_ChannelId(Long channelId);

    void deleteByChannel_ChannelId(Long channelId);
}
