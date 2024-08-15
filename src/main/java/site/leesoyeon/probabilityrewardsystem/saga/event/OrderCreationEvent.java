package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.ORDER_CREATION;

@Getter
@RequiredArgsConstructor
public class OrderCreationEvent implements SagaEvent {
    private final SagaEventType eventType = ORDER_CREATION;
    private final OrderContext context;
}