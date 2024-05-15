package webprogrammingTeam.matchingService.domain.board.dto.request;

import lombok.Builder;

@Builder
public record BoardSaveRequest(String title, String content){
}
