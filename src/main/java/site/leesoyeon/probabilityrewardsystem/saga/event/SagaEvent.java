package site.leesoyeon.probabilityrewardsystem.saga.event;

import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

public interface SagaEvent {
    SagaEventType getEventType();
    OrderContext getContext();
}
