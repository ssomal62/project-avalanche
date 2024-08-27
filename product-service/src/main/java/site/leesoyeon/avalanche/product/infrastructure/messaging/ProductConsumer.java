package site.leesoyeon.avalanche.product.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.command.CheckStockCommand;
import site.leesoyeon.avalanche.avro.command.ReleaseStockCommand;
import site.leesoyeon.avalanche.product.application.service.InventoryService;
import site.leesoyeon.avalanche.product.infrastructure.saga.SagaStateManager;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProductConsumer {

    private final InventoryService inventoryService;
    private final SagaStateManager sagaStateManager;

    @KafkaListener(topics = "${app.kafka.topics.check-stock}")
    public void handleCheckStockCommand(CheckStockCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("재고 확인 명령 수신: orderId={}, productId={}, quantity={}",
                        command.getOrderId(), command.getProductId(), command.getQuantity());
                inventoryService.checkAndReserveStock(command);
                ack.acknowledge();
                log.info("재고 확인 및 예약 처리 완료 및 커밋: orderId={}", command.getOrderId());
            } catch (Exception e) {
                log.error("재고 확인 및 예약 처리 중 오류 발생: orderId={}", command.getOrderId(), e);
            }
        }, ack);
    }

    @KafkaListener(topics = "${app.kafka.topics.release-stock}")
    public void handleReleaseStockCommand(ReleaseStockCommand command, Acknowledgment ack) {
        sagaStateManager.processCommandIfSagaStateValid(command.getOrderId(), () -> {
            try {
                log.info("재고 해제 명령 수신: orderId={}, productId={}, quantity={}",
                        command.getOrderId(), command.getProductId(), command.getQuantity());
                inventoryService.refundInventory(command);
                ack.acknowledge();
                log.info("재고 해제 처리 완료 및 커밋: orderId={}", command.getOrderId());
            } catch (Exception e) {
                log.error("재고 해제 처리 중 오류 발생: orderId={}", command.getOrderId(), e);
            }
        }, ack);
    }
}