package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

import java.time.LocalDateTime;


public record ProgramCategoryReadResponse(Long id, String title, Category category, Open open, LocalDateTime writingTime) {
}
