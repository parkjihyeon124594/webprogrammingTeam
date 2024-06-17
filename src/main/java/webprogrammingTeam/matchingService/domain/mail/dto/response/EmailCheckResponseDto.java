package webprogrammingTeam.matchingService.domain.mail.dto.response;

import lombok.Builder;

@Builder
public record EmailCheckResponseDto(
        String message
) {
}
