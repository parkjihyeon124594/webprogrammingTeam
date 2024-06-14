package webprogrammingTeam.matchingService.domain.subscription.dto;

import lombok.Getter;

@Getter
public class AddSubscriptionRequest {

    private Long memberId;

    private Long channelId;
}
