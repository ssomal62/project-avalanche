package site.leesoyeon.avalanche.user.infrastructure.exception;

import site.leesoyeon.avalanche.user.shared.api.ApiStatus;

public class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
        super(ApiStatus.INVALID_INPUT_VALUE);
    }

    public InvalidPasswordException(String debugMessage) {
        super(ApiStatus.INVALID_INPUT_VALUE, debugMessage);
    }
}