package site.leesoyeon.probabilityrewardsystem.saga.step;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.point.service.PointDeductionService;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointDeductionStep implements SagaStep<OrderContext> {

    private final PointDeductionService pointDeductionService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderContext execute(OrderContext context) {
        if (context.state() != SagaState.ORDER_CREATED) {
            return context.fail("포인트 차감을 실행할 수 있는 상태가 아닙니다.");
        }
        return transactionTemplate.execute(status -> pointDeductionService.deductPoints(context));
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (context.state() != SagaState.COMPENSATION_IN_PROGRESS) {
            return context;
        }

        if (!context.pointStepCompleted()) {
            log.info("포인트 차감이 이루어지지 않아 보상 작업이 생략되었습니다");
            return context;
        }

        return transactionTemplate.execute(status -> {
            try {
                return pointDeductionService.refundPoints(context);
            } catch (Exception e) {
                status.setRollbackOnly();
                return context.fail("포인트 환불 실패: " + e.getMessage());
            }
        });
    }
}