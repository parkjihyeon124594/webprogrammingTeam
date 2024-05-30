package webprogrammingTeam.matchingService.domain.board.dto.response;

import lombok.Builder;

@Builder
public record BoardAllReadResponse(Long id, String title, String date) {
}
