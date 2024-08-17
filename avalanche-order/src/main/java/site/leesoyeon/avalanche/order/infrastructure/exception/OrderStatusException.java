package site.leesoyeon.avalanche.order.infrastructure.exception;

import site.leesoyeon.avalanche.order.shared.api.ApiStatus;

public class OrderStatusException extends OrderException {

    public OrderStatusException() {
        super(ApiStatus.INVALID_INPUT_VALUE);
    }

    public OrderStatusException(String debugMessage) {
        super(ApiStatus.INVALID_INPUT_VALUE, debugMessage);
    }
}