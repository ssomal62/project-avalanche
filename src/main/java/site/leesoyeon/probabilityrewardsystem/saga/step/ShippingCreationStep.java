package site.leesoyeon.probabilityrewardsystem.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;
import site.leesoyeon.probabilityrewardsystem.shipping.service.ShippingCreationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingCreationStep implements SagaStep<OrderContext> {

    private final ShippingCreationService shippingCreationService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderContext execute(OrderContext context) {
        if (context.state() != SagaState.INVENTORY_DEDUCTED) {
            return context.fail("배송지 생성을 실행할 수 있는 상태가 아닙니다.");
        }
        return transactionTemplate.execute(status -> shippingCreationService.createShipping(context));
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (context.state() != SagaState.COMPENSATION_IN_PROGRESS) return context;

        if (!context.shippingStepCompleted()) {
            log.info("배송지 생성이 이루어지지 않아 보상 작업이 생략되었습니다.");
            return context;
        }

        return transactionTemplate.execute(status -> {
            try {
                return shippingCreationService.cancelShipping(context);
            } catch (Exception e) {
                status.setRollbackOnly();
                return context.fail("배송지 삭제 실패 : " + e.getMessage());
            }
        });
    }
}
