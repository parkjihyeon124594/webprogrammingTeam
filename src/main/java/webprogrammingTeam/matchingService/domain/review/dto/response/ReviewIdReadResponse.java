package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewIdReadResponse(Long reviewId, String title, int rating, String content, String date){
}
