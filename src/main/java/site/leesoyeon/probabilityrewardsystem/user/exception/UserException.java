package site.leesoyeon.probabilityrewardsystem.user.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class UserException extends RuntimeException {

    private final ApiStatus status;

    public UserException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
