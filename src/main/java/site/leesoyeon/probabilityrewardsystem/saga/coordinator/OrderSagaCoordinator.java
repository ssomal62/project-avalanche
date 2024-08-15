package site.leesoyeon.probabilityrewardsystem.saga.coordinator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.*;
import site.leesoyeon.probabilityrewardsystem.saga.exception.SagaException;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;
import site.leesoyeon.probabilityrewardsystem.saga.step.InventoryDeductionStep;
import site.leesoyeon.probabilityrewardsystem.saga.step.OrderCreationStep;
import site.leesoyeon.probabilityrewardsystem.saga.step.SagaStep;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaCoordinator implements SagaCoordinator<OrderContext> {

    private final InventoryDeductionStep inventoryDeductionStep;
    private final OrderCreationStep orderCreationStep;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    public OrderContext execute(OrderContext context) {
        return context.setState(SagaState.STARTED)
                .then(ctx -> executeStepAndPublish(ctx, inventoryDeductionStep, InventoryDeductionEvent.class))
                .then(ctx -> executeStepAndPublish(ctx, orderCreationStep, OrderCreationEvent.class))
                .then(OrderContext::completed);
    }

    private <T> OrderContext executeStepAndPublish(OrderContext context, SagaStep<OrderContext> step, Class<T> eventClass) {
        context = step.execute(context);
        if (context.isFailed()) {
            return handleFailure(context);
        }
        try {
            T event = eventClass.getConstructor(OrderContext.class).newInstance(context);
            eventPublisher.publish((SagaEvent) event);
        } catch (Exception e) {
            return context.fail("이벤트 발행 실패: " + e.getMessage());
        }
        return context;
    }

    private OrderContext handleFailure(OrderContext context) {
        eventPublisher.publish(new SagaFailedEvent(context));
        OrderContext compensatedContext = compensate(context);
        return compensatedContext.toBuilder()
                .state(SagaState.FAILED)
                .success(false)
                .build();
    }

    private OrderContext compensate(OrderContext context) {
        try {
            context = context.setState(SagaState.COMPENSATION_IN_PROGRESS);
            eventPublisher.publish(new CompensationEvent(context));

            context = orderCreationStep.compensate(context)
                    .then(inventoryDeductionStep::compensate);

            context = context.setState(SagaState.COMPENSATION_COMPLETED);
            eventPublisher.publish(new CompensationCompletedEvent(context));

        } catch (Exception e) {
            context = context.setState(SagaState.COMPENSATION_FAILED);
            eventPublisher.publish(new CompensationFailedEvent(context));
            throw new SagaException("보상 처리 실패", e);
        }
        return context;
    }
}