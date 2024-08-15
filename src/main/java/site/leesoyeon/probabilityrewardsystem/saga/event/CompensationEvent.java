package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.COMPENSATION_STARTED;

@Getter
@RequiredArgsConstructor
public class CompensationEvent implements SagaEvent {
    private final SagaEventType eventType = COMPENSATION_STARTED;
    private final OrderContext context;
}