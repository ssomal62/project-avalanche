package site.leesoyeon.avalanche.shipping.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.command.CancelShippingCommand;
import site.leesoyeon.avalanche.avro.command.PrepareShippingCommand;
import site.leesoyeon.avalanche.shipping.application.service.ShippingCreationService;
import site.leesoyeon.avalanche.shipping.infrastructure.saga.SagaStateManager;


@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingConsumer {

    private final ShippingCreationService shippingCreationService;
    private final SagaStateManager sagaStateManager;

    @KafkaListener(topics = "${app.kafka.topics.prepare-shipping}")
    public void handlePrepareShippingCommand(PrepareShippingCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("배송 준비 명령 수신: orderId={}", command.getOrderId());
                shippingCreationService.createShipping(command);
                ack.acknowledge();
                log.info("배송 준비 처리 완료 및 커밋: orderId={}", command.getOrderId());
            } catch (Exception e) {
                log.error("배송 준비 수신 중 오류 발생: orderId={}", command.getOrderId(), e);
            }

        }, ack);
    }

    @KafkaListener(topics = "${app.kafka.topics.cancel-shipping}")
    public void handleCancelShippingCommand(CancelShippingCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("배송 취소 명령 수신 - 주문 ID: {}", command.getOrderId());
                shippingCreationService.cancelShipping(command);
                ack.acknowledge();
                log.info("배송 취소 처리 완료 및 커밋 - 주문 ID: {}", command.getOrderId());
            } catch (Exception e) {
                log.error("배송 취소 처리 중 오류 발생 - 주문 ID: {}", command.getOrderId(), e);
            }
        }, ack);
    }
}