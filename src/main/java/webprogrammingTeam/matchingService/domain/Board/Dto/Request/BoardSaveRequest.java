package webprogrammingTeam.matchingService.domain.Board.Dto.Request;

import lombok.Builder;
import webprogrammingTeam.matchingService.User.User;

@Builder
public record BoardSaveRequest(String title, String content){
}
