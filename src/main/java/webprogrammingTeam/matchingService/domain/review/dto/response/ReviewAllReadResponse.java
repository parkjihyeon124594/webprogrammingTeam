package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewAllReadResponse(int rating, String content, String date) {
}
