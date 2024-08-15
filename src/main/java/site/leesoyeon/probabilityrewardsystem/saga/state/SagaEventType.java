package site.leesoyeon.probabilityrewardsystem.saga.state;

public enum SagaEventType {
    POINT_DEDUCTION,
    INVENTORY_DEDUCTION,
    ORDER_CREATION,
    SAGA_FAILED,
    COMPENSATION_STARTED,
    COMPENSATION_COMPLETED,
    COMPENSATION_FAILED
}