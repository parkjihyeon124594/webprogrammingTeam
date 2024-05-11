package webprogrammingTeam.matchingService.domain.Board.Dto.Response;

import lombok.Builder;
import webprogrammingTeam.matchingService.User.User;

@Builder
public record BoardAllReadResponse(User user, Long id, String title, String date) {
}
