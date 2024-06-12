package webprogrammingTeam.matchingService.domain.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewIdReadResponse(int rating, String content, String date){
}
