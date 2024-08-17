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
public class FinalizationStep implements SagaStep<OrderContext> {

    private final OrderCreationService orderCreationService;

    @Override
    public OrderContext execute(OrderContext context) {
        if(context.state() != SagaState.ORDER_FINALIZATION_PENDING) return context;
        return orderCreationService.updateOrderWithShippingInfo(context);
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        return null;
    }
}
