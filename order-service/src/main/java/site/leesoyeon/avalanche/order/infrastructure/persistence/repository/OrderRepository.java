package site.leesoyeon.avalanche.order.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.leesoyeon.avalanche.order.domain.model.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> , OrderQueryRepository {
}
