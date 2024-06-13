package webprogrammingTeam.matchingService.domain.refresh.exception;

import org.springframework.http.HttpStatus;
import webprogrammingTeam.matchingService.global.exception.ErrorCode;

public enum RefreshErrorcode implements ErrorCode {
    REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND,"NoneExistent RefreshToken");
    private final HttpStatus status;
    private final String message;

    RefreshErrorcode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }


    @Override
    public HttpStatus status() {
        return this.status;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return this.message;
    }
}
