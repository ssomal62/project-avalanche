package site.leesoyeon.avalanche.point.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.avro.command.ApplyPointCommand;
import site.leesoyeon.avalanche.point.application.util.PointTransactionMapper;
import site.leesoyeon.avalanche.point.domain.model.PointTransaction;
import site.leesoyeon.avalanche.point.infrastructure.exception.PointTransactionException;
import site.leesoyeon.avalanche.point.infrastructure.presentation.repository.PointTransactionRepository;
import site.leesoyeon.avalanche.point.presentation.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionDetailDto;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionListDto;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionSummaryDto;
import site.leesoyeon.avalanche.point.shared.api.ApiStatus;
import site.leesoyeon.avalanche.point.shared.enums.ActivityType;

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
    public PointTransaction adjustPoints(ApplyPointCommand command, Integer balance) {
        PointTransaction pointTransaction = pointTransactionMapper.toPointTransaction(command, balance);
        return pointTransactionRepository.save(pointTransaction);
    }

    @Transactional
    public void adjustPointsManually(ManualPointAdjustmentRequest request) {
        Integer lastBalance = getLastBalance(request.userId());

        PointTransaction pointTransaction = pointTransactionMapper.toEntity(request);

        pointTransaction.updateBalance(lastBalance + request.amount());
        pointTransactionRepository.save(pointTransaction);
    }

    @Transactional
    public void updateProductId(UUID transactionId, UUID productId) {
        PointTransaction transaction = findById(transactionId);
        transaction.updateProductId(productId);
    }

    /**
     * 특정 포인트 트랜잭션을 취소하고 환불 트랜잭션을 생성합니다.
     *
     * 이 메서드는 다음 작업을 수행합니다:
     * 1. 지정된 ID로 트랜잭션을 찾습니다.
     * 2. 찾은 트랜잭션을 취소 상태로 변경합니다.
     * 3. 원래 트랜잭션과 반대되는 금액으로 새로운 환불 트랜잭션을 생성합니다.
     * 4. 새로 생성된 환불 트랜잭션을 저장합니다.
     *
     * @param transactionId 취소할 트랜잭션의 고유 식별자
     */
    @Transactional
    public Integer cancelTransaction(UUID transactionId) {
        PointTransaction transaction = findById(transactionId);
        transaction.cancel();
        PointTransaction cancelTransaction = PointTransaction.builder()
                .userId(transaction.getUserId())
                .amount(-transaction.getAmount())
                .balance(getLastBalance(transaction.getUserId()))
                .activityType(ActivityType.REFUND)
                .description("트랜잭션 취소: " + transactionId)
                .build();
        pointTransactionRepository.save(cancelTransaction);
        return cancelTransaction.getBalance();
    }

//     ============================================
//                 Protected Methods
//     ============================================

    protected PointTransaction findById(UUID transactionId) {
        return pointTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new PointTransactionException(ApiStatus.NOT_FOUND_POINT_HISTORY));
    }

    protected Integer getLastBalance(UUID userId) {
        return pointTransactionRepository.findLatestActiveBalanceByUserId(userId).orElse(0);
    }

    public PointTransaction findByOrderIdAndNotCancelled(UUID orderId) {
        return pointTransactionRepository.findByOrderIdAndIsCancelledFalse(orderId)
                .orElse(null);
    }
}
