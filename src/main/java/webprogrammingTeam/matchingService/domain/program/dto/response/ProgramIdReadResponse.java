package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.program.entity.Category;
import webprogrammingTeam.matchingService.domain.program.entity.Open;

import java.util.List;

@Builder
public record ProgramIdReadResponse(Long programId,String memberEmail, String title, String writingTime, String content, Category category, int maximum, String recruitmentStartDate, String recruitmentEndDate, String programDate, Open open, List<String> images) {
}
