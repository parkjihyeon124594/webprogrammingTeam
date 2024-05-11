package webprogrammingTeam.matchingService.domain.Board.Dto.Response;

import lombok.Builder;

@Builder
public record BoardAllReadResponse(Long id, String title, String date) {
}
