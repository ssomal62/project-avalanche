package site.leesoyeon.avalanche.user.infrastructure.exception;

import site.leesoyeon.avalanche.user.shared.api.ApiStatus;

public class DuplicateEmailException extends UserException {

    public DuplicateEmailException() {
        super(ApiStatus.DUPLICATE_EMAIL);
    }

    public DuplicateEmailException(String debugMessage) {
        super(ApiStatus.DUPLICATE_EMAIL, debugMessage);
    }
}