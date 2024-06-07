package webprogrammingTeam.matchingService.domain.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagePayLoad {
    private Long senderId;
    private String content;
//    private boolean kickRequest;
//    private Long kickMemberId;
}
