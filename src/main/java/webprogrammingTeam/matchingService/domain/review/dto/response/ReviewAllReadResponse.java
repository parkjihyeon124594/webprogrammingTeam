package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.review.entity.Rating;

@Builder
public record ReviewAllReadResponse(Rating rating, String content, String date) {
}
