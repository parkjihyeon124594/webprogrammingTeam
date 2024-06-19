package webprogrammingTeam.matchingService.domain.subscription.dto;

import webprogrammingTeam.matchingService.domain.program.entity.Category;

public record PrivateChannelsResponse(Long channelId, Long programId, String title, Category category, String programDate, String imageUrl ) {
}
