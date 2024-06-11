package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;

@Builder
public record ProgramAllReadResponse(Long id, String title, Category category, String writingTime) {
}
