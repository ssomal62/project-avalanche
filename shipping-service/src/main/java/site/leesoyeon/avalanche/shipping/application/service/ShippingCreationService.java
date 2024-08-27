package site.leesoyeon.avalanche.shipping.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.avro.command.CancelShippingCommand;
import site.leesoyeon.avalanche.avro.command.PrepareShippingCommand;
import site.leesoyeon.avalanche.avro.command.ShippingData;
import site.leesoyeon.avalanche.avro.event.ShippingCancelledEvent;
import site.leesoyeon.avalanche.avro.event.ShippingPreparedEvent;
import site.leesoyeon.avalanche.shipping.domain.model.Shipping;
import site.leesoyeon.avalanche.shipping.infrastructure.messaging.ShippingProducer;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingCreationService {

    private final ShippingService shippingService;
    private final ShippingProducer shippingProducer;

    @Transactional
    public void createShipping(PrepareShippingCommand command) {
        String orderId = command.getOrderId();
        try {
            ShippingData shippingData = command.getShippingData();

            if (shippingData == null || shippingData.getRecipientName() == null || shippingData.getRecipientName().isEmpty()) {
                log.error("수령인 정보가 누락되었습니다: 주문 ID: {}", orderId);
                sendShippingPreparedResult(orderId, null, false);
                return;
            }

            Shipping shipping = shippingService.saveShipping(UUID.fromString(orderId), shippingData);
            log.info("배송 생성 완료 - 배송 ID: {}, 주문 ID: {}", shipping.getShippingId(), orderId);

            sendShippingPreparedResult(orderId, shipping.getShippingId().toString(), true);
        } catch (Exception e) {
            handleCreationFailure(orderId, e);
        }
    }

    @Transactional
    public void cancelShipping(CancelShippingCommand command) {
        String orderId = command.getOrderId();
        try {
            UUID shippingId = UUID.fromString(command.getShippingId());

            shippingService.deleteById(shippingId);
            log.info("배송 취소 완료 - 배송 ID: {}, 주문 ID: {}", shippingId, orderId);

            sendShippingCancelledResult(orderId, true);
        } catch (Exception e) {
            log.error("배송 취소 중 예외가 발생했습니다: 주문 ID: {}", orderId, e);
            sendShippingCancelledResult(orderId, false);
        }
    }

    private void handleCreationFailure(String orderId, Exception e) {
        try {
            log.error("배송 생성 중 예외가 발생했습니다: 주문 ID: {}", orderId, e);
            cancelShippingTransaction(orderId);
        } catch (Exception cancelException) {
            log.error("배송 생성 실패 후 취소 처리 중 오류 발생: 주문 ID: {}", orderId, cancelException);
        } finally {
            sendShippingPreparedResult(orderId, null, false);
        }
    }

    private void cancelShippingTransaction(String orderId) {
        try {
            Shipping shipping = shippingService.findByOrderId(UUID.fromString(orderId));
            if (shipping != null) {
                shippingService.deleteById(shipping.getShippingId());
                log.info("배송 트랜잭션 취소 완료: 주문 ID: {}, 배송 ID: {}", orderId, shipping.getShippingId());
            } else {
                log.info("취소할 배송 트랜잭션이 없습니다: 주문 ID: {}", orderId);
            }
        } catch (Exception e) {
            log.error("배송 트랜잭션 취소 중 오류 발생: 주문 ID: {}", orderId, e);
        }
    }

    private void sendShippingPreparedResult(String orderId, String shippingId, boolean isSuccess) {
        ShippingPreparedEvent event = ShippingPreparedEvent.newBuilder()
                .setOrderId(orderId)
                .setShippingId(shippingId)
                .setIsSuccess(isSuccess)
                .setTrackingNumber(isSuccess ? "CJ-" + UUID.randomUUID().toString().substring(0, 7) : null)
                .build();

        shippingProducer.sendShippingPreparedEvent(event);
    }

    private void sendShippingCancelledResult(String orderId, boolean isCancelSuccess) {
        ShippingCancelledEvent event = ShippingCancelledEvent.newBuilder()
                .setOrderId(orderId)
                .setIsCancelSuccess(isCancelSuccess)
                .build();

        shippingProducer.sendShippingCancelledEvent(event);
    }
}
