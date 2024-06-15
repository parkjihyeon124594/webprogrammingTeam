package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

@Builder
public record ProgramAllReadResponse(Long id, String title, Category category, Open open, String writingTime, String imageUrl, int recruitment, double avgRating, int ratingCnt) {
}
