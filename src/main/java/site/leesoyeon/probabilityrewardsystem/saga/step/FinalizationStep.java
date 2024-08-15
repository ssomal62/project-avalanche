package site.leesoyeon.probabilityrewardsystem.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.order.service.OrderCreationService;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.event.OrderCreationEvent;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalizationStep implements SagaStep<OrderContext> {

    private final OrderCreationService orderCreationService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderContext execute(OrderContext context) {
        if (context.state() != SagaState.SHIPPING_CREATED) {
            return context.fail("주문 완료를 실행할 수 있는 상태가 아닙니다.");
        }

        return transactionTemplate.execute(status -> {
            OrderContext orderContext = orderCreationService.updateOrderWithShippingInfo(context);
            eventPublisher.publish(new OrderCreationEvent(orderContext));
            return orderContext;
        });
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        return null;
    }
}
