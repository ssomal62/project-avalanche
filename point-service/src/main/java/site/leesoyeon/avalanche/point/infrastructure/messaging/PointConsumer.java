package site.leesoyeon.avalanche.point.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.command.ApplyPointCommand;
import site.leesoyeon.avalanche.avro.command.RefundPointCommand;
import site.leesoyeon.avalanche.point.application.service.PointDeductionService;
import site.leesoyeon.avalanche.point.infrastructure.saga.SagaStateManager;


@Slf4j
@Component
@RequiredArgsConstructor
public class PointConsumer {

    private final PointDeductionService pointDeductionService;
    private final SagaStateManager sagaStateManager;

    @KafkaListener(topics = "${app.kafka.topics.apply-point}")
    public void handleApplyPointCommand(ApplyPointCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("포인트 차감 명령 수신: orderId={}, userId={}, amount={}",
                        command.getOrderId(), command.getUserId(), command.getAmount());
                pointDeductionService.deductPoints(command);
                ack.acknowledge();
                log.info("포인트 차감 처리 완료 및 커밋: orderId={}", command.getOrderId());
            } catch (Exception e) {
                log.error("포인트 차감 수신 중 오류 발생: orderId={}", command.getOrderId(), e);
            }
        }, ack);
    }

    @KafkaListener(topics = "${app.kafka.topics.refund-point}")
    public void handleRefundPointsCommand(RefundPointCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("포인트 환불 명령 수신: orderId={}, pointId={}, amount={}",
                        command.getOrderId(), command.getPointId(), command.getAmount());
                pointDeductionService.refundPoints(command);
                ack.acknowledge();
                log.info("포인트 환불 처리 완료 및 커밋: orderId={}", command.getOrderId());
            } catch (Exception e) {
                log.error("포인트 환불 수신 중 오류 발생: orderId={}", command.getOrderId(), e);
            }
        }, ack);
    }
}