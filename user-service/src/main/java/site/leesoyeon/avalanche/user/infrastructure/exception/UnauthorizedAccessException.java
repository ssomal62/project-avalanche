package site.leesoyeon.avalanche.user.infrastructure.exception;

import site.leesoyeon.avalanche.user.shared.api.ApiStatus;

public class UnauthorizedAccessException extends UserException {

    public UnauthorizedAccessException() {
        super(ApiStatus.NO_AUTHORIZATION);
    }

    public UnauthorizedAccessException(String debugMessage) {
        super(ApiStatus.NO_AUTHORIZATION, debugMessage);
    }
}