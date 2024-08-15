package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

@Getter
@RequiredArgsConstructor
public class OrderCreationEvent implements SagaEvent {
    private final String eventType = "ORDER_CREATION";
    private final OrderContext context;
}