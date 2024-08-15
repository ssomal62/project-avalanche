package site.leesoyeon.probabilityrewardsystem.saga.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

@Getter
@RequiredArgsConstructor
public class InventoryDeductionEvent implements SagaEvent {
    private final String eventType = "INVENTORY_DEDUCTION";
    private final OrderContext context;
}