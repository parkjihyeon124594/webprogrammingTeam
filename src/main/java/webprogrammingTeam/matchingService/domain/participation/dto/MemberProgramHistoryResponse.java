package webprogrammingTeam.matchingService.domain.participation.dto;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;

@Builder
public record MemberProgramHistoryResponse(Long programId, String title, Category category, String programDate){
}
