package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;

import static site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType.INVENTORY_DEDUCTION;

@Getter
@RequiredArgsConstructor
public class InventoryDeductionEvent implements SagaEvent {
    private final SagaEventType eventType = INVENTORY_DEDUCTION;
    private final OrderContext context;
}