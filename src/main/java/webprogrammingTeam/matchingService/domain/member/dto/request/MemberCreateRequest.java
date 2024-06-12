package webprogrammingTeam.matchingService.domain.member.dto.request;

public record MemberCreateRequest(
        String memberName,
        String email,
        String password,
        String birth,
        String gender,
        Double latitude,
        Double longitude,
        int age
) {
}
