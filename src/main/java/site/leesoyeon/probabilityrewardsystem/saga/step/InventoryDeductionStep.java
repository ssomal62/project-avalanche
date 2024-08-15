package site.leesoyeon.probabilityrewardsystem.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.product.service.InventoryService;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.event.InventoryDeductionEvent;
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
//        if (context.state() != SagaState.POINT_DEDUCTED) {
        if (context.state() != SagaState.STARTED) {
            return context.fail("제고 공제를 실행할 수 있는 상태가 아닙니다.");
        }

        return transactionTemplate.execute(status -> {
            OrderContext result = inventoryService.deductInventory(context);
            if (!result.success()) {
                status.setRollbackOnly();
                log.error("재고 공제가 실패했습니다: {}", context.orderItem().productId());
                return result;
            }
            log.info("제품에 대한 재고가 성공적으로 공제되었습니다: {}", context.orderItem().productId());
            return result;
        });
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (context.state() != SagaState.COMPENSATION_IN_PROGRESS) {
            log.warn("예기치 않은 보상 상태: {}", context.state());
            return context;
        }

        if (context.outOfStock()) {
            log.info("재고 부족으로 인해 보상 작업이 생략되었습니다: {}", context.orderItem().productId());
            return context;
        }

        return transactionTemplate.execute(status -> {
            try {
                OrderContext refundedContext = inventoryService.refundInventory(context);
                log.info("제품에 대한 재고가 성공적으로 환불되었습니다: {}", context.orderItem().productId());

                eventPublisher.publish(new InventoryDeductionEvent(refundedContext));
                return refundedContext;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("제품에 대한 재고를 환불하지 못했습니다.: {}", context.orderItem().productId(), e);
                return context.fail("재고 환불 실패: " + e.getMessage());
            }
        });
    }
}