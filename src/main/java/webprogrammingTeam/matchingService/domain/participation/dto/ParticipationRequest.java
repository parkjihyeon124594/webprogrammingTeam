package webprogrammingTeam.matchingService.domain.participation.dto;

import lombok.Builder;

@Builder
public record ParticipationRequest(Long programId, Long memberId){
}
