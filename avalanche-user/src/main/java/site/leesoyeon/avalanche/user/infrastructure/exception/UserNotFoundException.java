package site.leesoyeon.avalanche.user.infrastructure.exception;

import site.leesoyeon.avalanche.user.shared.api.ApiStatus;

public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super(ApiStatus.NOT_FOUND_USER);
    }

    public UserNotFoundException(String debugMessage) {
        super(ApiStatus.NOT_FOUND_USER, debugMessage);
    }
}