package webprogrammingTeam.matchingService.domain.subscription.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberChannelSubscriptionDTO {

    private Long memberId;

    private Long channelId;
}
