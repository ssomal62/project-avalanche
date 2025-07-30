package site.leesoyeon.avalanche.order.infrastructure.exception;

public class OrderSagaException extends RuntimeException {
    public OrderSagaException(String message) {
        super(message);
    }

    public OrderSagaException(String message, Throwable cause) {
        super(message, cause);
    }
}