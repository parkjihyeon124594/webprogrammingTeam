package webprogrammingTeam.matchingService.domain.program.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
public record CategoryAgeGroupResponse(
        String city,
        String category,
        int teen,
        int twenties,
        int thirties,
        int forties,
        int fifties,
        int sixties,
        int seventies,
        int eighties
) {
}

