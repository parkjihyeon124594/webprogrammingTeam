package webprogrammingTeam.matchingService.domain.channel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import webprogrammingTeam.matchingService.domain.channel.dto.ChannelTitleDTO;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.repository.ChannelRepository;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    private Channel publicChannel;
    private Channel privateChannel;

    @BeforeEach
    void setUp() {
        publicChannel = new Channel();
        publicChannel.setChannelId(1L);
        publicChannel.setTitle("Public Channel");
        publicChannel.setPublic(true);

        privateChannel = new Channel();
        privateChannel.setChannelId(2L);
        privateChannel.setTitle("Private Channel");
        privateChannel.setPublic(false);
    }

    @Test
    void testGetAllPublicChannelTitles() {
        ChannelTitleDTO dto = mock(ChannelTitleDTO.class);
        when(dto.getTitle()).thenReturn(publicChannel.getTitle());
        when(channelRepository.findAllProjectedByIsPublicTrue()).thenReturn(Arrays.asList(dto));

        List<ChannelTitleDTO> result = channelService.getAllPublicChannelTitles();
        assertEquals(1, result.size());
        assertEquals("Public Channel", result.get(0).getTitle());
    }

    @Test
    void testCreatePublicChannel() {
        when(channelRepository.save(any(Channel.class))).thenReturn(publicChannel);

        Channel result = channelService.createPublicChannel("Public Channel");
        assertNotNull(result);
        assertTrue(result.isPublic());
        assertEquals("Public Channel", result.getTitle());
    }

    @Test
    void testCreatePrivateChannel() {
        when(channelRepository.save(any(Channel.class))).thenReturn(privateChannel);

        Channel result = channelService.createPrivateChannel("Private Channel");
        assertNotNull(result);
        assertFalse(result.isPublic());
        assertEquals("Private Channel", result.getTitle());
    }

    @Test
    void testGetChannelById() {
        when(channelRepository.findById(1L)).thenReturn(Optional.of(publicChannel));

        Channel result = channelService.getChannelById(1L);
        assertNotNull(result);
        assertEquals("Public Channel", result.getTitle());
    }

    @Test
    void testGetChannelById_NotFound() {
        when(channelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> channelService.getChannelById(1L));
    }

    @Test
    void testDeleteChannel() {
        when(channelRepository.existsById(1L)).thenReturn(true);

        channelService.deleteChannel(1L);

        verify(channelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteChannel_NotExist() {
        when(channelRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> channelService.deleteChannel(1L));
    }
}
