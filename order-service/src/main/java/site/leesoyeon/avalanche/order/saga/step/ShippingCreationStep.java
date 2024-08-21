package site.leesoyeon.avalanche.order.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.infrastructure.external.client.ShippingServiceClient;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingCreationStep implements SagaStep<OrderContext> {

    private final ShippingServiceClient shippingServiceClient;

    @Override
    public OrderContext execute(OrderContext context) {
        if(context.state() != SagaState.SHIPPING_REGISTRATION_PENDING) return context;
        return shippingServiceClient.createShipping(context);
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (!context.shippingStepCompleted()) {
            log.info("배송지 보상 트랜잭션이 생략되었습니다.");
            return context;
        }
        return shippingServiceClient.cancelShipping(context);
    }
}
