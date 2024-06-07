package webprogrammingTeam.matchingService.domain.board.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.board.entity.Category;

@Builder
public record BoardAllReadResponse(Long id, String title, Category category,String writingTime) {
}
