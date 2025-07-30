package site.leesoyeon.avalanche.order.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.presentation.dto.OrderSearchCondition;

public interface OrderQueryRepository {
    Page<Order> findOrdersWithCondition(OrderSearchCondition condition, Pageable pageable);
}