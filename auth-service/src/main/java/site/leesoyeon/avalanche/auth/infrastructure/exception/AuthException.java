package site.leesoyeon.avalanche.auth.infrastructure.exception;

import lombok.Getter;
import site.leesoyeon.avalanche.auth.shared.api.ApiStatus;

@Getter
public class AuthException extends RuntimeException {

    private final ApiStatus status;

    public AuthException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}