package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewIdReadResponse(Long reviewId, int rating, String content, String date){
}
