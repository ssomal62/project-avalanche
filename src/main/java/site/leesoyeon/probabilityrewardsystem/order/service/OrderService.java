package site.leesoyeon.probabilityrewardsystem.order.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderDetailDto;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderListDto;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderSearchCondition;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.exception.OrderException;
import site.leesoyeon.probabilityrewardsystem.order.repository.OrderRepository;
import site.leesoyeon.probabilityrewardsystem.order.util.OrderMapper;

import java.util.UUID;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_ORDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderDetailDto getOrderDetail(UUID orderId) {
        Order order = findOrderById(orderId);
        return orderMapper.toOrderDetailDto(order);
    }

    public OrderListDto getOrderList(@Valid OrderSearchCondition condition, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findOrdersWithCondition(condition, pageable);
        Page<OrderDetailDto> orderDetailDtoPage = orderPage.map(orderMapper::toOrderDetailDto);
        return new OrderListDto(orderDetailDtoPage);
    }

//     ============================================
//                  Private Methods
//     ============================================

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));
    }
}
