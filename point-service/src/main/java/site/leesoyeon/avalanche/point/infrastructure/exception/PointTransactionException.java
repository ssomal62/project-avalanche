package site.leesoyeon.avalanche.point.infrastructure.exception;

import lombok.Getter;
import site.leesoyeon.avalanche.point.shared.api.ApiStatus;
@Getter
public class PointTransactionException extends RuntimeException {

    private final ApiStatus status;
    private final String debugMessage;

    public PointTransactionException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = null;
    }

    public PointTransactionException(ApiStatus status, String debugMessage) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = debugMessage;
    }
}