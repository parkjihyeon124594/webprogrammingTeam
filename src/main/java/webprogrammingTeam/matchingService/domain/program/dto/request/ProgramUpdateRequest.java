package webprogrammingTeam.matchingService.domain.program.dto.request;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

@Builder
public record ProgramUpdateRequest(String title, String content, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate, Open open, Double latitude, Double longitude, String programAddress){
}
