package site.leesoyeon.probabilityrewardsystem.saga.event;

import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

public interface SagaEvent {
    String getEventType();
    OrderContext getContext();
}
