package webprogrammingTeam.matchingService.domain.Board.Dto.Request;

import lombok.Builder;

@Builder
public record BoardSaveRequest(String title, String content){
}
