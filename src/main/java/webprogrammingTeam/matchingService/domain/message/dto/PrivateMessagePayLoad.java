package webprogrammingTeam.matchingService.domain.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateMessagePayLoad {
    private Long senderId;
    private String content;
}
