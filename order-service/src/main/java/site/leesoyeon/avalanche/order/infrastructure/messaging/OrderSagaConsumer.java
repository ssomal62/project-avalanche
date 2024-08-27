package site.leesoyeon.avalanche.order.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.event.*;
import site.leesoyeon.avalanche.order.infrastructure.saga.OrderSagaManager;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaConsumer {

    private final OrderSagaManager sagaManager;

    @KafkaListener(topics = "${app.kafka.topics.stock-checked}")
    public void handleStockChecked(StockCheckedEvent event, Acknowledgment ack) {
        try {
            log.info("재고 확인 이벤트 수신: {}", event.getOrderId());
            sagaManager.handleStockChecked(UUID.fromString(event.getOrderId()), event.getIsSuccess());
            ack.acknowledge();
            log.info("재고 확인 처리 완료 및 커밋: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("재고 확인 수신 중 오류 발생: {}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.point-applied}")
    public void handlePointApplied(PointAppliedEvent event, Acknowledgment ack) {
        String orderId = event.getOrderId();
        log.info("포인트 적용 이벤트 수신: {}, 성공 여부: {}", orderId, event.getIsSuccess());

        try {
            UUID pointId = event.getPointId() == null ? null : UUID.fromString(event.getPointId());
            sagaManager.handlePointApplied(UUID.fromString(orderId), pointId, event.getIsSuccess());

            if (!event.getIsSuccess()) {
                log.warn("포인트 적용 실패: {}", orderId);
            }
        } catch (Exception e) {
            log.error("포인트 적용 처리 중 오류 발생: {}", orderId, e);
        } finally {
            ack.acknowledge();
            log.info("포인트 적용 이벤트 처리 완료: {}", orderId);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.shipping-prepared}")
    public void handleShippingPrepared(ShippingPreparedEvent event, Acknowledgment ack) {
        try {
            sagaManager.handleShippingPrepared(UUID.fromString(event.getOrderId()), event.getShippingId() == null ? null : UUID.fromString(event.getShippingId()), event.getIsSuccess());
            ack.acknowledge();
            log.info("배송 준비 처리 완료 및 커밋: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("배송 준비 수신 중 오류 발생: {}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.shipping-cancelled}")
    public void handleShippingCancelled(ShippingCancelledEvent event, Acknowledgment ack) {
        try {
            sagaManager.handleShippingCancelled(UUID.fromString(event.getOrderId()), event.getIsCancelSuccess());
            ack.acknowledge();
            log.info("배송 취소 처리 완료 및 커밋: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("배송 취소 수신 중 오류 발생: {}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.point-refunded}")
    public void handlePointRefunded(PointRefundedEvent event, Acknowledgment ack) {
        try {
            sagaManager.handlePointsRefunded(UUID.fromString(event.getOrderId()), event.getIsRefundSuccess());
            ack.acknowledge();
            log.info("포인트 환불 처리 완료 및 커밋: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("포인트 수신 처리 중 오류 발생: {}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.stock-released}")
    public void handleStockReleased(StockReleasedEvent event, Acknowledgment ack) {
        try {
            sagaManager.handleStockReleased(UUID.fromString(event.getOrderId()), event.getIsReleaseSuccess());
            ack.acknowledge();
            log.info("재고 해제 처리 완료 및 커밋: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("재고 해제 수신 중 오류 발생: {}", event.getOrderId(), e);
        }
    }

}