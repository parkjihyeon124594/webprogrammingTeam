package webprogrammingTeam.matchingService.domain.channel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateChannelRequest {
    private String title;
}
