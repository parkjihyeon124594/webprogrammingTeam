package webprogrammingTeam.matchingService.domain.message.service;


import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.entity.Message;
import webprogrammingTeam.matchingService.domain.message.repository.MessageRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    
    private final ChannelService channelService;
    
    private final MemberService memberService;

    @Autowired
    public MessageService(MessageRepository messageRepository, ChannelService channelService, MemberService memberService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.memberService = memberService;
    }

    public MessageDTO addMessage(Long channelId, Long senderId, String content) {
        Channel channel = channelService.getChannelById(channelId);
        Member member = memberService.getMemberById(senderId);
        
        Message message = new Message();
        message.setChannel(channel);
        message.setSender(member);
        message.setContent(content);
        
        Message newMessage = messageRepository.save(message);
        
        MessageDTO messageDTO = convertMessageToMessageDTO(newMessage);
        
        return messageDTO;
    }

    private MessageDTO convertMessageToMessageDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setMessageId(message.getMessageId());
        messageDTO.setChannelId(message.getChannel().getChannelId());
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setContent(message.getContent());

        return messageDTO;
    }

    public List<MessageDTO> findAllMessageByChannelId(Long channelId) {
        List<Message> allMessages = messageRepository.getAllMessagesByChannel_ChannelId(channelId);

        List<MessageDTO> messageDTOList =  convertMessagesToMessagesDTO(allMessages);
        return messageDTOList;
    }

    private List<MessageDTO> convertMessagesToMessagesDTO(List<Message> messages) {
        return messages.stream()
                .map(this::convertMessageToMessageDTO)
                .collect(Collectors.toList());
    }
    public void deleteAllMessageByChannelId(Long channelId) {
        messageRepository.deleteByChannel_ChannelId(channelId);
    }

    public void delete(Long messageId) {
        messageRepository.deleteById(messageId);
    }
}
