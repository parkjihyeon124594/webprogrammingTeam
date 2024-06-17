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
    }

    public List<ChannelTitleDTO> getAllPublicChannelTitles() {
        return channelRepository.findAllProjectedByIsPublicTrue();
    }

    // getAllPrivateChannelTitles 는 user가 참여했는지를 확인해야하기 때문에, subscription에 있어야 됨.

    public Channel createPublicChannel(String title) {
        Channel channel = new Channel();
        channel.setTitle(title);
        channel.setPublic(true);

        Channel newPublicChannel = channelRepository.save(channel);

        return newPublicChannel;
    }

    public Channel createPrivateChannel(String title) {
        Channel channel = new Channel();
        channel.setTitle(title);
        channel.setPublic(false);

        Channel newPrivateChannel = channelRepository.save(channel);

        return newPrivateChannel;
    }

    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found with id " + channelId));
    }

    public void deleteChannel(Long channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("Channel with ID " + channelId + " does not exist");
        }
        channelRepository.deleteById(channelId);
    }
}
