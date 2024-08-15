package site.leesoyeon.probabilityrewardsystem.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.leesoyeon.probabilityrewardsystem.shipping.entity.Shipping;

import java.util.UUID;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
}
