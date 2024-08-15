package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.SHIPPING_CREATED;

@Getter
@RequiredArgsConstructor
public class ShippingCreationEvent implements SagaEvent {
    private final SagaEventType eventType = SHIPPING_CREATED;
    private final OrderContext context;
}
