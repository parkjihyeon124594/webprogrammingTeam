package webprogrammingTeam.matchingService.domain.Review.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.Review.entity.Rating;

@Builder
public record ReviewIdReadResponse(Rating rating, String content, String date){
}
