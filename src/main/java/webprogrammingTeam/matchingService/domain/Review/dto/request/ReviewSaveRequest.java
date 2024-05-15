package webprogrammingTeam.matchingService.domain.Review.dto.request;

import lombok.Builder;
@Builder
public record ReviewSaveRequest(float rating, String content, String date){
}
