package webprogrammingTeam.matchingService.domain.message.dto;

import lombok.Getter;
import lombok.Setter;

public record PublicMessagePayLoad(Long senderId, String content) {

}
