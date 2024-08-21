package site.leesoyeon.avalanche.order.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.infrastructure.external.client.InventoryServiceClient;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryDeductionStep implements SagaStep<OrderContext> {

    private final InventoryServiceClient inventoryServiceClient;

    @Override
    public OrderContext execute(OrderContext context) {
        if(context.state() != SagaState.INVENTORY_DEDUCTION_PENDING) return context;
        return inventoryServiceClient.deductInventory(context);
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (!context.inventoryStepCompleted()) {
            log.info("재고 보상 트랜잭션이 생략되었습니다.");
            return context;
        }
        return inventoryServiceClient.refundInventory(context);
    }
}
