package site.leesoyeon.avalanche.order.infrastructure.saga.enums;

public enum OrderSagaStatus {

    STARTED,
    COMPLETED,
    FAILED,
    CANCELLED,
    COMPENSATING;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}