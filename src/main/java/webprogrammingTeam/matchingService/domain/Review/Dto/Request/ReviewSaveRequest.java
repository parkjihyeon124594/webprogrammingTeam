package webprogrammingTeam.matchingService.domain.Review.Dto.Request;

import lombok.Builder;
@Builder
public record ReviewSaveRequest(float rating, String content, String date){
}
