package site.leesoyeon.probabilityrewardsystem.email.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class EmailException extends RuntimeException {

    private final ApiStatus status;

    public EmailException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
