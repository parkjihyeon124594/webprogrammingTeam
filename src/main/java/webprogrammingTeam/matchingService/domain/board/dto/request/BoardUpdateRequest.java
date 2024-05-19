package webprogrammingTeam.matchingService.domain.board.dto.request;

import lombok.Builder;

@Builder
public record BoardUpdateRequest(String title, String content){
}
