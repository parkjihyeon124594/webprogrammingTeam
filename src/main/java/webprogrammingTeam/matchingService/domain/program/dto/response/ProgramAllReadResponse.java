package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

import java.time.LocalDateTime;

@Builder
public record ProgramAllReadResponse(Long id, String title, Category category, Open open, java.time.LocalDateTime writingTime, String imageUrl) {

}
