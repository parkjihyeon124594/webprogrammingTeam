package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;

@Builder
public record Test(
        String age,
        int sports,
        int computer,
        int art
) {
}