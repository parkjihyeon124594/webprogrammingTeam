package webprogrammingTeam.matchingService.domain.board.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.board.entity.Category;

@Builder
public record BoardUpdateRequest(String title, String content, Category category,String recruitmentStartDate, String recruitmentEndDate, String programDate){
}
