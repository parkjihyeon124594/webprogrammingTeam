package webprogrammingTeam.matchingService.domain.program.dto.response;

public record MonthlyCategoryCountResponse(
        String month,
        String category,
        Long programCount
) {
}
