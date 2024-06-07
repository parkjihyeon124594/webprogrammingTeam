package webprogrammingTeam.matchingService.domain.review.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.review.entity.Rating;

@Builder
public record ReviewUpdateRequest(Rating rating,String title,  String content) {
}
