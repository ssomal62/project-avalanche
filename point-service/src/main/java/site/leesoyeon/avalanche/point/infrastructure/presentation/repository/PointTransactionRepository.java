package site.leesoyeon.avalanche.point.infrastructure.presentation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.leesoyeon.avalanche.point.domain.model.PointTransaction;

import java.util.Optional;
import java.util.UUID;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID>, PointTransactionRepositoryCustom {

    Optional<PointTransaction> findByOrderIdAndIsCancelledFalse(UUID orderId);
}
