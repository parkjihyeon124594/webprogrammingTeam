package webprogrammingTeam.matchingService.domain.program.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;

@Builder
public record ProgramUpdateRequest(String title, String content, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate ){
}
