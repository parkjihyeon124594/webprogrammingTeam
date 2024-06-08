package webprogrammingTeam.matchingService.domain.program.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;

@Builder
public record ProgramSaveRequest(String title, String content, Category category, String recruitmentStartDate, String recruitmentEndDate, String programDate){
}
