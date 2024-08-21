package site.leesoyeon.avalanche.order.saga.coordinator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.infrastructure.exception.SagaException;
import site.leesoyeon.avalanche.order.saga.state.SagaState;
import site.leesoyeon.avalanche.order.saga.step.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaCoordinator implements SagaCoordinator<OrderContext> {

    private final OrderCreationStep orderCreationStep;
    private final PointDeductionStep pointDeductionStep;
    private final InventoryDeductionStep inventoryDeductionStep;
    private final ShippingCreationStep shippingCreationStep;
    private final FinalizationStep finalizationStep;

    public OrderContext execute(OrderContext context) {
            return context.setState(SagaState.ORDER_PENDING)
                    .then(ctx -> executeStep(ctx, orderCreationStep, SagaState.POINT_DEDUCTION_PENDING))
                    .then(ctx -> executeStep(ctx, pointDeductionStep, SagaState.INVENTORY_DEDUCTION_PENDING))
                    .then(ctx -> executeStep(ctx, inventoryDeductionStep, SagaState.SHIPPING_REGISTRATION_PENDING))
                    .then(ctx -> executeStep(ctx, shippingCreationStep, SagaState.ORDER_FINALIZATION_PENDING))
                    .then(ctx -> executeStep(ctx, finalizationStep, SagaState.ORDER_FINALIZED))
                    .then(this::completed);
    }

    private OrderContext executeStep(OrderContext context, SagaStep<OrderContext> step, SagaState nextState) {
        if (context.isFailed()) {
            return context;
        }
        OrderContext resultContext = step.execute(context);
        if (resultContext.isFailed()) {
            return handleFailure(resultContext);
        }
        log.info("{} 완료. state : {}, id : {}", step.getClass().getSimpleName(), nextState, resultContext.orderId());
        return resultContext.setState(nextState);
    }

    private OrderContext handleFailure(OrderContext context) {
        context = compensate(context);
        return context.toBuilder()
                .state(SagaState.ORDER_CREATION_FAILED)
                .success(false)
                .build();
    }

    public OrderContext completed(OrderContext context) {
        return context.toBuilder()
                .state(SagaState.ORDER_FINALIZED)
                .success(true)
                .build();
    }

    private OrderContext compensate(OrderContext context) {
        try {
            context = context.setState(SagaState.COMPENSATION_IN_PROGRESS);

            context = shippingCreationStep.compensate(context)
                    .then(inventoryDeductionStep::compensate)
                    .then(pointDeductionStep::compensate)
                    .then(orderCreationStep::compensate);
            return context.setState(SagaState.COMPENSATION_COMPLETED);
        } catch (Exception e) {
            throw new SagaException("Compensation step failed.", context);
        }
    }
}
