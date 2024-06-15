package webprogrammingTeam.matchingService.domain.program.dto.response;

import java.util.List;

public record CategoryAgeGroupListResponse(
        List<CategoryAgeGroupResponse> ageGroups
) {
}
