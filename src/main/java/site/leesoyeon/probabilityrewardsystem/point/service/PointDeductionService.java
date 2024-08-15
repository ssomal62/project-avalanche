package site.leesoyeon.probabilityrewardsystem.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointDeductionService {

    private final PointTransactionService pointTransactionService;

    @Transactional
    public OrderContext deductPoints(OrderContext context) {
        try {
            UUID userId = context.userId();
            Integer amount = context.transactionInfo().amount();
            Integer lastBalance = pointTransactionService.getLastBalance(userId);

            if (lastBalance < amount) {
                log.error("포인트 부족: 사용자 ID: {}, 요청된 포인트: {}, 현재 잔액: {}", userId, amount, lastBalance);
                return context.fail("포인트 부족: 요청된 포인트를 차감할 수 없습니다.");
            }
            PointTransaction transaction = pointTransactionService.adjustPoints(context, lastBalance);
            log.info("포인트 조정 완료: 사용자 ID: {}, 변경 금액: {}, 현재 잔액: {}, 활동 유형: {}",
                    userId, transaction.getAmount(), transaction.getBalance(), transaction.getActivityType());

            return context.pointDeducted(transaction);
        } catch (Exception e) {
            return context.fail("포인트 차감 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext refundPoints(OrderContext context) {
        try {
            Integer lastBalance = pointTransactionService.cancelTransaction(context.transactionInfo().transactionId());
            log.info("포인트 환불 완료. 사용자 ID: {}, 환불된 포인트: {}, 현재 잔액: {}",
                    context.userId(), context.productInfo().unitPrice(), lastBalance);
            return context.pointRefunded();
        } catch (Exception e) {
            return context.fail("포인트 환불 중 오류 발생: " + e.getMessage());
        }
    }
}
