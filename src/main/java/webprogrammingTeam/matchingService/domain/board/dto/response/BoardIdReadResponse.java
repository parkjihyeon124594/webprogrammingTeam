package webprogrammingTeam.matchingService.domain.board.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.board.entity.Category;

import java.util.List;

@Builder
public record BoardIdReadResponse(String title, String writingTime, String content, Category category, String recruitmentStartDate, String recruitmentEndDate, String programDate, List<byte[]> imagesByte) {
}
