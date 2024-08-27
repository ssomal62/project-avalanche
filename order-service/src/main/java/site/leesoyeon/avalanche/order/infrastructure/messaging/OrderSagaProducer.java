package site.leesoyeon.avalanche.order.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.command.*;
import site.leesoyeon.avalanche.order.presentation.dto.ShippingInfo;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.check-stock}")
    private String CHECK_STOCK_TOPIC;

    @Value("${app.kafka.topics.apply-point}")
    private String APPLY_POINTS_TOPIC;

    @Value("${app.kafka.topics.prepare-shipping}")
    private String PREPARE_SHIPPING_TOPIC;

    @Value("${app.kafka.topics.release-stock}")
    private String RELEASE_STOCK_TOPIC;

    @Value("${app.kafka.topics.refund-point}")
    private String REFUND_POINT_TOPIC;

    @Value("${app.kafka.topics.cancel-shipping}")
    private String CANCEL_SHIPPING_TOPIC;


    //============================================================================================

    public void sendReleaseStockCommand(UUID orderId, UUID productId, Integer quantity) {
        ReleaseStockCommand command = ReleaseStockCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setProductId(productId.toString())
                .setQuantity(quantity)
                .build();

        kafkaTemplate.send(RELEASE_STOCK_TOPIC, orderId.toString(), command);

        log.info("재고 해제 명령 전송 완료 - 주문 ID: {}, 제품 ID: {}, 수량: {}", orderId, productId, quantity);
    }

    public void sendRefundPointCommand(UUID orderId, UUID pointId, Integer amount) {
        RefundPointCommand command = RefundPointCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setPointId(pointId.toString())
                .setAmount(amount)
                .build();

        kafkaTemplate.send(REFUND_POINT_TOPIC, orderId.toString(), command);

        log.info("포인트 환불 명령 전송 완료 - 주문 ID: {}, 포인트 ID: {}, 금액: {}", orderId, pointId, amount);
    }

    public void sendCancelShippingCommand(UUID orderId, UUID shippingId) {
        CancelShippingCommand command = CancelShippingCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setShippingId(shippingId.toString())
                .build();

        kafkaTemplate.send(CANCEL_SHIPPING_TOPIC, orderId.toString(), command);

        log.info("배송 취소 명령 전송 완료 - 주문 ID: {}, 배송 ID: {}", orderId, shippingId);
    }

    //============================================================================================

    public void sendCheckStockCommand(UUID orderId, String productId, int quantity) {
        CheckStockCommand command = CheckStockCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        kafkaTemplate.send(CHECK_STOCK_TOPIC, orderId.toString(), command);

        log.info("재고 확인 명령 전송 완료 - 주문 ID: {}, 제품 ID: {}, 수량: {}", orderId, productId, quantity);
    }

    public void sendApplyPointsCommand(UUID orderId, UUID userId, Integer amount, String activityType, String productName) {
        ApplyPointCommand command = ApplyPointCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setUserId(userId.toString())
                .setAmount(amount)
                .setActivityType(activityType)
                .setProductName(productName)
                .build();

        kafkaTemplate.send(APPLY_POINTS_TOPIC, orderId.toString(), command);

        log.info("포인트 적용 명령 전송 완료 - 주문 ID: {}, 사용자 ID: {}, 금액: {}, 활동 유형: {}, 제품명: {}", orderId, userId, amount, activityType, productName);
    }

    public void sendPrepareShippingCommand(UUID orderId, ShippingInfo shippingInfo) {
         PrepareShippingCommand command = PrepareShippingCommand.newBuilder()
                .setOrderId(orderId.toString())
                .setShippingData(ShippingData.newBuilder()
                        .setRecipientName(shippingInfo.recipientName())
                        .setRecipientPhone(shippingInfo.recipientPhone())
                        .setAddress(shippingInfo.address())
                        .setDetailedAddress(shippingInfo.detailedAddress())
                        .setZipCode(shippingInfo.zipCode())
                        .build()
                )
                .build();

        kafkaTemplate.send(PREPARE_SHIPPING_TOPIC, orderId.toString(), command);

        log.info("배송 준비 명령 전송 완료 - 주문 ID: {}, 수령인: {}", orderId, shippingInfo.recipientName());
    }

}
