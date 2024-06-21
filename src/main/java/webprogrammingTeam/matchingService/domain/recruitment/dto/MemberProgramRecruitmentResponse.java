package webprogrammingTeam.matchingService.domain.recruitment.dto;

import lombok.Builder;
import webprogrammingTeam.matchingService.domain.program.entity.Category;

@Builder
public record MemberProgramRecruitmentResponse(Long programId, String title, Category category, String programDate, String imageUrl, String programAddress){
}
