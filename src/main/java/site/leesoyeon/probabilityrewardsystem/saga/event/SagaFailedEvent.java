package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.SAGA_FAILED;

@Getter
@RequiredArgsConstructor
public class SagaFailedEvent implements SagaEvent {
    private final SagaEventType eventType = SAGA_FAILED;
    private final OrderContext context;
}