package webprogrammingTeam.matchingService.domain.subscription.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChannelSubscriptionDTO {

    private Long userId;

    private Long channelId;
}
