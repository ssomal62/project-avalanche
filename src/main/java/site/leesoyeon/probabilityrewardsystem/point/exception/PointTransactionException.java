package site.leesoyeon.probabilityrewardsystem.point.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class PointTransactionException extends RuntimeException {

    private final ApiStatus status;

    public PointTransactionException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}