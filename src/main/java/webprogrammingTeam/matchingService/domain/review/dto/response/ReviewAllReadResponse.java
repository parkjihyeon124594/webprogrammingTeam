package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewAllReadResponse(Long reviewId, String senderEmail, String title, int rating, String content, String date) {
}
