package site.leesoyeon.avalanche.order.saga.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.infrastructure.external.client.PointServiceClient;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointDeductionStep implements SagaStep<OrderContext> {

    private final PointServiceClient pointServiceClient;

    @Override
    public OrderContext execute(OrderContext context) {
        if(context.state() != SagaState.POINT_DEDUCTION_PENDING) return context;
        return pointServiceClient.deductPoints(context);
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (!context.pointStepCompleted()) {
            log.info("포인트 보상 트랜잭션이 생략되었습니다.");
            return context;
        }
        return pointServiceClient.refundPoints(context);
    }
}