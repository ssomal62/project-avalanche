package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.POINT_DEDUCTION;

@Getter
@RequiredArgsConstructor
public class PointDeductionEvent implements SagaEvent {
    private final SagaEventType eventType = POINT_DEDUCTION;
    private final OrderContext context;
}