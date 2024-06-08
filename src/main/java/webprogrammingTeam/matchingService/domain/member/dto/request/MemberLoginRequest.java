package webprogrammingTeam.matchingService.domain.member.dto.request;

public record MemberLoginRequest (
        String email,
        String password
){
}
