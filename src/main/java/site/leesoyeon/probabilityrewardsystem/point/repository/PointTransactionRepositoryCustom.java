package site.leesoyeon.probabilityrewardsystem.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionSearchCondition;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionSummaryDto;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointTransactionRepositoryCustom {

    List<PointTransaction> findPointTransactionWithCondition(PointTransactionSearchCondition condition);
    Page<PointTransactionSummaryDto> findAllByUserId(UUID userId, Pageable pageable);
    Optional<Integer> findLatestActiveBalanceByUserId(UUID userId);
}
