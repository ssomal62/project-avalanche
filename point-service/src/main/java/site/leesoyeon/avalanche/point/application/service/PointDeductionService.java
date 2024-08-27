package site.leesoyeon.avalanche.point.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.avro.command.ApplyPointCommand;
import site.leesoyeon.avalanche.avro.command.RefundPointCommand;
import site.leesoyeon.avalanche.avro.event.PointAppliedEvent;
import site.leesoyeon.avalanche.avro.event.PointRefundedEvent;
import site.leesoyeon.avalanche.point.domain.model.PointTransaction;
import site.leesoyeon.avalanche.point.infrastructure.messaging.PointProducer;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointDeductionService {

    private final PointTransactionService pointTransactionService;
    private final PointProducer pointProducer;

    @Transactional
    public void deductPoints(ApplyPointCommand command) {
        UUID userId = UUID.fromString(command.getUserId());
        String orderId = command.getOrderId();
        int amount = command.getAmount();

        try {
            int lastBalance = pointTransactionService.getLastBalance(userId);

            if (lastBalance < amount) {
                log.error("포인트 잔액 부족으로 차감 실패: 주문 ID: {}, 현재 잔액: {}, 차감 금액: {} ", orderId, lastBalance, amount);
                sendPointAppliedResult(orderId, null, false);
                return;
            }

            PointTransaction transaction = pointTransactionService.adjustPoints(command, lastBalance);
            log.info("포인트 차감 완료: 사용자 ID: {}, 차감 금액: {}, 현재 잔액: {}, 활동 유형: {}",
                    userId, transaction.getAmount(), transaction.getBalance(), transaction.getActivityType());

            sendPointAppliedResult(orderId, transaction.getTransactionId().toString(), true);
        } catch (Exception e) {
            handleDeductionFailure(orderId, e);
        }
    }

    @Transactional
    public void refundPoints(RefundPointCommand command) {
        String orderId = command.getOrderId();
        try {
            cancelTransaction(orderId);
            sendPointRefundedResult(orderId, true);
        } catch (Exception e) {
            log.error("포인트 환불 중 예외 발생: 주문 ID: {}", orderId, e);
            sendPointRefundedResult(orderId, false);
        }
    }

    private void handleDeductionFailure(String orderId, Exception e) {
        try {
            cancelTransaction(orderId);
        } catch (Exception cancelException) {
            log.error("포인트 차감 실패 후 취소 처리 중 오류 발생: {}", orderId, cancelException);
        } finally {
            sendPointAppliedResult(orderId, null , false);
        }
    }

    private void cancelTransaction(String orderId) {
        PointTransaction transaction = pointTransactionService.findByOrderIdAndNotCancelled(UUID.fromString(orderId));
        if (transaction == null) {
            log.info("취소할 트랜잭션이 없거나 이미 취소됨: 주문 ID: {}", orderId);
            return;
        }

        int lastBalance = pointTransactionService.cancelTransaction(transaction.getTransactionId());
        log.info("포인트 트랜잭션 취소 완료: 주문 ID: {}, 취소된 포인트: {}, 현재 잔액: {}",
                orderId, Math.abs(transaction.getAmount()), lastBalance);
    }

    private void sendPointAppliedResult(String orderId, String pointId, boolean isSuccess) {
        PointAppliedEvent event = PointAppliedEvent.newBuilder()
                .setOrderId(orderId)
                .setPointId(pointId)
                .setIsSuccess(isSuccess)
                .build();

        pointProducer.sendPointAppliedEvent(event);
    }

    private void sendPointRefundedResult(String orderId, boolean isRefundSuccess) {
        PointRefundedEvent event = PointRefundedEvent.newBuilder()
                .setOrderId(orderId)
                .setIsRefundSuccess(isRefundSuccess)
                .build();

        pointProducer.sendPointRefundedEvent(event);
    }
}
