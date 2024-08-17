package site.leesoyeon.avalanche.point.infrastructure.presentation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionSearchCondition;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionSummaryDto;
import site.leesoyeon.avalanche.point.domain.model.PointTransaction;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointTransactionRepositoryCustom {

    List<PointTransaction> findPointTransactionWithCondition(PointTransactionSearchCondition condition);
    Page<PointTransactionSummaryDto> findAllByUserId(UUID userId, Pageable pageable);
    Optional<Integer> findLatestActiveBalanceByUserId(UUID userId);
}
