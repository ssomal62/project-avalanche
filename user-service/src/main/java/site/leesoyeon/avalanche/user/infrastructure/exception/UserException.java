package site.leesoyeon.avalanche.user.infrastructure.exception;

import lombok.Getter;
import site.leesoyeon.avalanche.user.shared.api.ApiStatus;

@Getter
public class UserException extends RuntimeException {

    private final ApiStatus status;
    private final String debugMessage;

    public UserException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = null;
    }

    public UserException(ApiStatus status, String debugMessage) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = debugMessage;
    }
}