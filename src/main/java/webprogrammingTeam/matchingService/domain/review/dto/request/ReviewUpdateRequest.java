package webprogrammingTeam.matchingService.domain.review.dto.request;

import lombok.Builder;

@Builder
public record ReviewUpdateRequest(int rating,String title,  String content) {
}
