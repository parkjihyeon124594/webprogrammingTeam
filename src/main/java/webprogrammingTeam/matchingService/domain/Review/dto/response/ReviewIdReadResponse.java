package webprogrammingTeam.matchingService.domain.Review.dto.response;

import lombok.Builder;

@Builder
public record ReviewIdReadResponse(float rating, String content, String date){
}
