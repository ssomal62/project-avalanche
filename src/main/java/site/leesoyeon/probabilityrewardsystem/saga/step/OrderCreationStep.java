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
import site.leesoyeon.probabilityrewardsystem.shipping.service.ShippingCreationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationStep implements SagaStep<OrderContext> {

    private final OrderCreationService orderCreationService;
    private final ShippingCreationService shippingCreationService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderContext execute(OrderContext context) {
        if (context.state() != SagaState.INVENTORY_DEDUCTED) {
            return context.fail("주문 생성을 실행할 수 있는 상태가 아닙니다.");
        }

        return transactionTemplate.execute(status -> {
            try {
                // 주문 생성
                OrderContext orderContext = orderCreationService.createOrder(context);
                if (orderContext.isFailed()) {
                    status.setRollbackOnly();
                    return orderContext;
                }
                log.info("주문 생성 성공: {}", orderContext.orderId());

                // 배송지 생성
                orderContext = shippingCreationService.createShipping(orderContext);
                if (orderContext.isFailed()) {
                    log.error("배송 생성 실패: {}", orderContext.errorMessage());
                    status.setRollbackOnly();
                    return orderContext;
                }
                log.info("배송지 생성 성공: {}", orderContext.shippingInfo().shippingId());

                // 주문 완료 처리
                OrderContext completedContext = orderCreationService.updateOrderWithShippingInfo(orderContext);
                if (completedContext.isFailed()) {
                    status.setRollbackOnly();
                    return completedContext;
                }
                log.info("주문 완료 처리 완료: {}", completedContext.orderId());

                eventPublisher.publish(new OrderCreationEvent(completedContext));
                return completedContext;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("주문 생성 과정 중 오류가 발생했습니다.", e);
                return context.fail("주문 생성 실패: " + e.getMessage());
            }
        });
    }

    @Override
    public OrderContext compensate(OrderContext context) {
        if (context.state() != SagaState.COMPENSATION_IN_PROGRESS) {
            log.warn("예기치 않은 보상 상태: {}", context.state());
            return context;
        }

        return transactionTemplate.execute(status -> {
            try {
                OrderContext updatedContext = context;

                // 주문이 생성된 경우 주문 취소
                if (updatedContext.orderId() != null) {
                    updatedContext = orderCreationService.cancelOrder(updatedContext);
                    log.info("주문 취소 처리 완료. 새로운 상태: {}", updatedContext);
                }

                // 배송이 생성된 경우 배송 취소
                if (updatedContext.shippingInfo().shippingId() != null) {
                    updatedContext = shippingCreationService.cancelShipping(updatedContext);
                    log.info("배송지 취소 처리 완료. 새로운 상태: {}", updatedContext);
                }

                // 보상 완료 상태로 업데이트하고 이벤트 발행
                eventPublisher.publish(new OrderCreationEvent(updatedContext));
                return updatedContext;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("주문 생성 보상 처리 중 오류가 발생했습니다.", e);
                return context.fail("주문취소 및 배송취소 실패 : " + e.getMessage());
            }
        });
    }

}