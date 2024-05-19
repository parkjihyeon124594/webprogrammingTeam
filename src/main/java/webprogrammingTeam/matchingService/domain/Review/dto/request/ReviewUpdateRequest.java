package webprogrammingTeam.matchingService.domain.Review.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.Review.entity.Rating;

@Builder
public record ReviewUpdateRequest(Rating rating, String content) {
}
