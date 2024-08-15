package site.leesoyeon.probabilityrewardsystem.saga.coordinator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.event.*;
import site.leesoyeon.probabilityrewardsystem.saga.exception.SagaException;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;
import site.leesoyeon.probabilityrewardsystem.saga.step.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaCoordinator implements SagaCoordinator<OrderContext> {

    private final OrderCreationStep orderCreationStep;
    private final PointDeductionStep pointDeductionStep;
    private final InventoryDeductionStep inventoryDeductionStep;
    private final ShippingCreationStep shippingCreationStep;
    private final FinalizationStep finalizationStep;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    public OrderContext execute(OrderContext context) {
        try {
            return context.setState(SagaState.STARTED)
                    .then(ctx -> executeStep(ctx, orderCreationStep))
                    .then(ctx -> executeStep(ctx, pointDeductionStep))
                    .then(ctx -> executeStep(ctx, inventoryDeductionStep))
                    .then(ctx -> executeStep(ctx, shippingCreationStep))
                    .then(ctx -> executeStep(ctx, finalizationStep))
                    .then(OrderContext::completed);
        } catch (SagaException e) {
            return handleFailure(e.getContext());
        }
    }

    private OrderContext executeStep(OrderContext context, SagaStep<OrderContext> step) {
        OrderContext result = step.execute(context);
        StepCompletedEvent event = StepCompletedEvent.builder()
                .stepName(step.getClass().getSimpleName())
                .sagaState(result.state())
                .success(result.success())
                .details("Step completed: " + step.getClass().getSimpleName())
                .build();
        eventPublisher.publish(event);
        if (result.isFailed()) {
            throw new SagaException("Step failed: " + step.getClass().getSimpleName(), result);
        }
        return result;
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

            context = shippingCreationStep.compensate(context)
                    .then(inventoryDeductionStep::compensate)
                    .then(pointDeductionStep::compensate)
                    .then(orderCreationStep::compensate);

            context = context.setState(SagaState.COMPENSATION_COMPLETED);
            eventPublisher.publish(new CompensationCompletedEvent(context));
        } catch (Exception e) {
            context = context.setState(SagaState.COMPENSATION_FAILED);
            eventPublisher.publish(new CompensationFailedEvent(context));
            throw new SagaException("Step failed: ",context );
        }
        return context;
    }
}