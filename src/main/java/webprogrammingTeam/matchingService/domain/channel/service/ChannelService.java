package webprogrammingTeam.matchingService.domain.channel.service;

import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.repository.ChannelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    } // 어차피 구독 클래스는 user와 channle의 service에 종속 되어 있으니, 그냥 거기서 만들면 되는 거 아닌가? 만들라고 하고 id만 반환하면 되지.
    // createChannel을 createSubscription이 호출하면 됨.


    public Channel createChannel(String title) {
        Channel channel = new Channel();
        channel.setTitle(title);
        channelRepository.save(channel);
        return channel;
    }

    public List<ChannelTitleDTO> getAllChannelTitles() {
        return channelRepository.findAllProjectedBy();
    }

    public void deleteChannel(Long channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("Channel with ID " + channelId + " does not exist");
        }
        channelRepository.deleteById(channelId);
    }

    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with id " + channelId));
    }
}
