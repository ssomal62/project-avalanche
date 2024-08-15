package site.leesoyeon.probabilityrewardsystem.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderSearchCondition;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;

public interface OrderRepositoryCustom {
    Page<Order> findOrdersWithCondition(OrderSearchCondition condition, Pageable pageable);
}
