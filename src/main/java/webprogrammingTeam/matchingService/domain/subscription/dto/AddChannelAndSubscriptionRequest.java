package webprogrammingTeam.matchingService.domain.subscription.dto;

import lombok.Getter;

@Getter
public class AddChannelAndSubscriptionRequest {

    private Long userId;

    private String channelTitle;
}
