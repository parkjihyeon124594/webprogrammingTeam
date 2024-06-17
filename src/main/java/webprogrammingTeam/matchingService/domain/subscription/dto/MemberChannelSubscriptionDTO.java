package webprogrammingTeam.matchingService.domain.subscription.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberChannelSubscriptionDTO {

    private String memberName;

    private Long channelId;
}
