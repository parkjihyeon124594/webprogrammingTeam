package webprogrammingTeam.matchingService.domain.review.dto.request;

import lombok.Builder;

@Builder
public record ReviewSaveRequest(int rating, String title, String content){
}
