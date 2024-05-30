package webprogrammingTeam.matchingService.domain.board.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardIdReadResponse(String tile, String date, String content, List<byte[]> imagesByte) {
}
