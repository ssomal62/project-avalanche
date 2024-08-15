package site.leesoyeon.probabilityrewardsystem.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.product.service.InventoryService;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryDeductionStep implements SagaStep<OrderContext> {

    private final InventoryService inventoryService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderContext execute(OrderContext context) {
        if (context.state() != SagaState.POINT_DEDUCTED) {
            return context.fail("제고 공제를 실행할 수 있는 상태가 아닙니다.");
        }
        return transactionTemplate.execute(status -> inventoryService.deductInventory(context));
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (context.state() != SagaState.COMPENSATION_IN_PROGRESS) return context;

        if (!context.inventoryStepCompleted()) {
            log.info("재고 차감이 이루어지지 않아 보상 작업이 생략되었습니다.");
            return context;
        }

        return transactionTemplate.execute(status -> {
            try {
                return inventoryService.refundInventory(context);
            } catch (Exception e) {
                status.setRollbackOnly();
                return context.fail("재고 복구 실패: " + e.getMessage());
            }
        });
    }
}