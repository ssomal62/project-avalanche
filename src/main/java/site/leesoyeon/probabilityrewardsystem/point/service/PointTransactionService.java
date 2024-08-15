package site.leesoyeon.probabilityrewardsystem.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionSummaryDto;
import site.leesoyeon.probabilityrewardsystem.point.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionDetailDto;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionListDto;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;
import site.leesoyeon.probabilityrewardsystem.point.exception.PointTransactionException;
import site.leesoyeon.probabilityrewardsystem.point.repository.PointTransactionRepository;
import site.leesoyeon.probabilityrewardsystem.point.util.PointTransactionMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;
    private final PointTransactionMapper pointTransactionMapper;

    @Transactional(readOnly = true)
    public PointTransactionDetailDto getPointTransactionDetail(UUID transactionId) {
        PointTransaction transaction = findById(transactionId);
        return pointTransactionMapper.toDto(transaction);
    }

    @Transactional(readOnly = true)
    public PointTransactionListDto getPointTransactions(UUID userId, Pageable pageable) {
        Page<PointTransactionSummaryDto> transactionsPage = pointTransactionRepository.findAllByUserId(userId, pageable);
        return new PointTransactionListDto(transactionsPage);
    }

    @Transactional
    public void adjustPointsManually(ManualPointAdjustmentRequest request) {
        Integer lastBalance = getLastBalance(request.userId());

        PointTransaction pointTransaction = pointTransactionMapper.toEntity(request);

        pointTransaction.updateBalance(lastBalance + request.amount());
        pointTransactionRepository.save(pointTransaction);
    }

//     ============================================
//                 Protected Methods
//     ============================================

    protected PointTransaction findById(UUID transactionId) {
        return pointTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new PointTransactionException(ApiStatus.NOT_FOUND_POINT_HISTORY));
    }

    protected Integer getLastBalance(UUID userId) {
        PointTransaction lastTransaction = pointTransactionRepository.findTopByUserIdOrderByCreatedDateDesc(userId);
        return lastTransaction != null ? lastTransaction.getBalance() : 0;
    }

}
