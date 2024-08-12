package site.leesoyeon.probabilityrewardsystem.auth.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class AuthException extends RuntimeException {

    private final ApiStatus status;

    public AuthException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}