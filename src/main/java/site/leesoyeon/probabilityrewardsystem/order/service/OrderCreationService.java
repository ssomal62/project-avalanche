package site.leesoyeon.probabilityrewardsystem.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;
import site.leesoyeon.probabilityrewardsystem.order.exception.OrderException;
import site.leesoyeon.probabilityrewardsystem.order.exception.OrderStatusException;
import site.leesoyeon.probabilityrewardsystem.order.repository.OrderRepository;
import site.leesoyeon.probabilityrewardsystem.order.util.OrderMapper;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

import java.util.UUID;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_ORDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreationService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderContext createOrder(OrderContext context) {

        try {
            Order order = orderMapper.toEntity(context);
            order.updateStatus(OrderStatus.CREATED);
            order = orderRepository.save(order);
            return context.orderCreated(order.getOrderId());
        } catch (Exception e) {
            return context.fail("주문 생성에 실패하였습니다: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext cancelOrder(OrderContext context) {
        try {
            orderRepository.deleteById(context.orderId());
            return context.orderCancelled();
        } catch (Exception e) {
            return context.fail("주문 취소에 실패하였습니다: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext updateOrderWithShippingInfo(OrderContext context) {
        try {
            Order order = findOrderById(context.orderId());

            if (!OrderStatus.CREATED.equals(order.getStatus())) {
                throw new OrderStatusException("주문이 이미 완료되었습니다: " + context.orderId());
            }

            order.updateShippingId(context.shippingInfo().shippingId());
            order.updateStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            return context.completeOrder();
        } catch (Exception e) {
            return context.fail("주문 업데이트에 실패하였습니다: " + e.getMessage());
        }
    }

//     ============================================
//                  Private Methods
//     ============================================

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }
}
