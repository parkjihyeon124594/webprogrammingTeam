package webprogrammingTeam.matchingService.domain.recruitment.dto;

import lombok.Builder;

@Builder
public record ProgramRecruitmentResponse(String name, String email, int birth, String gender) {
}
