package webprogrammingTeam.matchingService.domain.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicMessagePayLoad {
    private Long senderId;
    private String content;
}
