package webprogrammingTeam.matchingService.domain.participation.dto;

import lombok.Builder;

@Builder
public record ProgramParticipationResponse(String name, String email, String birth, String gender) {
}
