package site.leesoyeon.probabilityrewardsystem.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;
import site.leesoyeon.probabilityrewardsystem.order.exception.OrderStatusException;
import site.leesoyeon.probabilityrewardsystem.order.util.OrderMapper;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreationService {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderContext createOrder(OrderContext context) {
        try {
            Order order = orderMapper.toEntity(context);
            order.updateStatus(OrderStatus.CREATED);
            order = orderService.saveOrder(order);
            log.info("주문 생성 성공 : {}", order.getOrderId());
            return context.orderCreated(order.getOrderId());
        } catch (Exception e) {
            return context.fail("주문 생성에 실패하였습니다: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext cancelOrder(OrderContext context) {
        try {
            orderService.deleteOrderById(context.orderId());
            log.info("주문 취소처리가 완료되었습니다.");
            return context.orderCancelled();
        } catch (Exception e) {
            return context.fail("주문 취소에 실패하였습니다: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext updateOrderWithShippingInfo(OrderContext context) {
        try {
            Order order = orderService.findOrderById(context.orderId());

            if (!OrderStatus.CREATED.equals(order.getStatus())) {
                throw new OrderStatusException("주문이 이미 완료되었습니다: " + context.orderId());
            }

            order.updateShippingId(context.shippingInfo().shippingId());
            order.updateStatus(OrderStatus.COMPLETED);
            orderService.saveOrder(order);
            log.info("최종 주문 업데이트 완료 : {}", order.getOrderId());
            return context.completeOrder();
        } catch (Exception e) {
            return context.fail("주문 업데이트에 실패하였습니다: " + e.getMessage());
        }
    }
}
