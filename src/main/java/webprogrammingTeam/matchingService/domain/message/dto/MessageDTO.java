package webprogrammingTeam.matchingService.domain.message.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageDTO {

    private Long messageId;

    private Long channelId;

    private String senderEmail;

    private String content;

    private String createTime;
}
