package webprogrammingTeam.matchingService.domain.mail.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record EmailCheckRequestDto(

        @NotEmpty(message = "이메일을 입력해주세요")
        String email,
        @NotEmpty
        String authNum
) {
}
