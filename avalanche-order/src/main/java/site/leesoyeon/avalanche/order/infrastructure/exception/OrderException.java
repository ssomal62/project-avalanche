package site.leesoyeon.avalanche.order.infrastructure.exception;

import lombok.Getter;
import site.leesoyeon.avalanche.order.shared.api.ApiStatus;

@Getter
public class OrderException extends RuntimeException {

    private final ApiStatus status;
    private final String debugMessage;

    public OrderException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = null;
    }

    public OrderException(ApiStatus status, String debugMessage) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = debugMessage;
    }
}