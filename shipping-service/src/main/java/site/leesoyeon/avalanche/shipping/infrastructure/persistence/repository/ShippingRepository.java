package site.leesoyeon.avalanche.shipping.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.leesoyeon.avalanche.shipping.domain.model.Shipping;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
    Optional<Shipping> findByOrderId(UUID orderId);
}
