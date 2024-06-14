package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewAllReadResponse(Long reviewId, int rating, String content, String date) {
}
