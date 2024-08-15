package site.leesoyeon.probabilityrewardsystem.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom  {

    Optional<Order> findByShippingId(UUID shippingId);
}
