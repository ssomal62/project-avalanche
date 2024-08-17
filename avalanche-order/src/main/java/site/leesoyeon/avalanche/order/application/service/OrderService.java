package site.leesoyeon.avalanche.order.application.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.order.shared.api.ApiStatus;
import site.leesoyeon.avalanche.order.presentation.dto.OrderDetailDto;
import site.leesoyeon.avalanche.order.presentation.dto.OrderListDto;
import site.leesoyeon.avalanche.order.presentation.dto.OrderSearchCondition;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.infrastructure.exception.OrderException;
import site.leesoyeon.avalanche.order.infrastructure.persistence.repository.OrderRepositoryImpl;
import site.leesoyeon.avalanche.order.application.util.OrderMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepositoryImpl orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(UUID orderId) {
        Order order = findOrderById(orderId);
        return orderMapper.toOrderDetailDto(order);
    }

    @Transactional(readOnly = true)
    public OrderListDto getOrderList(@Valid OrderSearchCondition condition, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findOrdersWithCondition(condition, pageable);
        Page<OrderDetailDto> orderDetailDtoPage = orderPage.map(orderMapper::toOrderDetailDto);
        return new OrderListDto(orderDetailDtoPage);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrderById(UUID orderId) {
        orderRepository.deleteById(orderId);
    }

//     ============================================
//                 Protected Methods
//     ============================================

    protected Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderException(ApiStatus.NOT_FOUND_ORDER));
    }
}
