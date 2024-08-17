package site.leesoyeon.avalanche.order.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.saga.state.SagaState;
import site.leesoyeon.avalanche.order.application.service.OrderCreationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationStep implements SagaStep<OrderContext> {

    private final OrderCreationService orderCreationService;

    @Override
    public OrderContext execute(OrderContext context) {
        if(context.state() != SagaState.ORDER_PENDING) return context;
        return orderCreationService.createOrder(context);
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (!context.orderStepCompleted()) {
            log.info("주문 보상 트랜잭션이 생략되었습니다.");
            return context;
        }
        return orderCreationService.cancelOrder(context);
    }
}