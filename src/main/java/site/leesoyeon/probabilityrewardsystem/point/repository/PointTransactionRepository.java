package site.leesoyeon.probabilityrewardsystem.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;

import java.util.UUID;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID>, PointTransactionRepositoryCustom{

    PointTransaction findTopByUserIdOrderByCreatedDateDesc(UUID userId);
}
