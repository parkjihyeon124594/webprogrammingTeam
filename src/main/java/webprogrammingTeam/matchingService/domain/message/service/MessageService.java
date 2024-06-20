package webprogrammingTeam.matchingService.domain.message.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import webprogrammingTeam.matchingService.auth.principal.PrincipalDetails;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.channel.service.ChannelService;
import webprogrammingTeam.matchingService.domain.message.dto.MessageDTO;
import webprogrammingTeam.matchingService.domain.message.entity.Message;
import webprogrammingTeam.matchingService.domain.message.repository.MessageRepository;
import webprogrammingTeam.matchingService.domain.member.entity.Member;
import webprogrammingTeam.matchingService.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    
    private final ChannelService channelService;
    
    private final MemberService memberService;

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;

    @Autowired
    public MessageService(MessageRepository messageRepository, ChannelService channelService, MemberService memberService, MemberChannelSubscriptionService memberChannelSubscriptionService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.memberService = memberService;
        this.memberChannelSubscriptionService = memberChannelSubscriptionService;
    }

    public List<MessageDTO> findAllMessageByPublicChannelId(Long channelId) {
        List<Message> allMessages = messageRepository.getAllMessagesByChannel_ChannelId(channelId);

        List<MessageDTO> messageDTOList =  convertMessagesToMessagesDTO(allMessages);
        return messageDTOList;
    }

    public List<MessageDTO> findAllMessageByPrivateChannelId(Long channelId, PrincipalDetails principalDetails) {
        if (memberChannelSubscriptionService.isSubscriber(channelId, principalDetails.getMember().getId())) {
            List<Message> allMessages = messageRepository.getAllMessagesByChannel_ChannelId(channelId);

            List<MessageDTO> messageDTOList =  convertMessagesToMessagesDTO(allMessages);
            return messageDTOList;
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Member is not a participant in the specified channel");
        }
    }

    private List<MessageDTO> convertMessagesToMessagesDTO(List<Message> messages) {
        return messages.stream()
                .map(this::convertMessageToMessageDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO addMessage(Long channelId, Long senderId, String content) {
        channelService.getAllPublicChannelTitles();
        Channel channel = channelService.getChannelById(channelId);
        Member member = memberService.getMemberById(senderId);

        log.info("{}", channel.getChannelId());
        log.info("{}", member.getEmail());

        Message message = new Message();
        message.setChannel(channel);
        message.setSender(member);
        message.setContent(content);
        message.setCreateDate(LocalDateTime.now());

        Message newMessage = messageRepository.save(message);

        MessageDTO messageDTO = convertMessageToMessageDTO(newMessage);

        Message savedMessage = messageRepository.findById(newMessage.getMessageId()).orElseThrow();
        log.info("저장된 메세지: " + savedMessage.getContent());
        return messageDTO;
    }

    public static String writingTimeToString(LocalDateTime writingTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.of("Asia/Seoul"));

        log.info("시간 {}",writingTime.atZone(ZoneId.of("Asia/Seoul")).format(formatter));
        return writingTime.atZone(ZoneId.of("Asia/Seoul")).format(formatter);
    }
    private MessageDTO convertMessageToMessageDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setMessageId(message.getMessageId());
        messageDTO.setChannelId(message.getChannel().getChannelId());
        messageDTO.setSenderEmail(message.getSender().getEmail());
        messageDTO.setContent(message.getContent());
        messageDTO.setCreateTime(writingTimeToString(message.getCreateDate()));

        return messageDTO;
    }
    public void deleteAllMessageByChannelId(Long channelId) {
        messageRepository.deleteByChannel_ChannelId(channelId);
        log.info("delete message");
    }

    public void delete(Long messageId) {
        messageRepository.deleteById(messageId);
    }
}
