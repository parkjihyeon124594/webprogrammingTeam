package webprogrammingTeam.matchingService.domain.Board.Dto.Response;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardIdReadResponse(String tile, String date, String content, List<byte[]> imagesByte) {
}
