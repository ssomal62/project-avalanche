package site.leesoyeon.avalanche.product.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.avro.command.CheckStockCommand;
import site.leesoyeon.avalanche.avro.command.ReleaseStockCommand;
import site.leesoyeon.avalanche.avro.event.StockCheckedEvent;
import site.leesoyeon.avalanche.avro.event.StockReleasedEvent;
import site.leesoyeon.avalanche.product.infrastructure.messaging.ProductProducer;
import site.leesoyeon.avalanche.product.infrastructure.redis.ProductCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductService productService;
    private final ProductProducer productProducer;
    private final ProductCacheManager productCacheManager;
    private final RedissonClient redissonClient;

    @Transactional
    public void checkAndReserveStock(CheckStockCommand command) {
        UUID productId = UUID.fromString(command.getProductId());
        String orderId = command.getOrderId();
        int quantity = command.getQuantity();
//        int getStock = productService.findStockById(productId);
//        productCacheManager.setStock(productId,getStock );


        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (isAlreadyReserved(orderId)) {
                sendStockCheckedResult(orderId, true);
                return;
            }

            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    if (reserveStockAndUpdateOrder(productId, orderId, quantity)) {
                        sendStockCheckedResult(orderId, true);
                    } else {
                        sendStockCheckedResult(orderId, false);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("재고 예약을 위한 락 획득 실패: 제품 ID: {}", productId);
                sendStockCheckedResult(orderId, false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            handleReservationError(orderId, productId, quantity, e);
        } catch (Exception e) {
            handleReservationError(orderId, productId, quantity, e);
        }
    }

    @Transactional
    public void refundInventory(ReleaseStockCommand command) {
        UUID productId = UUID.fromString(command.getProductId());
        String orderId = command.getOrderId();
        int quantity = command.getQuantity();

        try {
            if (!isReservedOrder(orderId)) {
                log.info("재고 복구 스킵: 예약되지 않은 주문. 주문 ID: {}", orderId);
                sendStockReleasedResult(orderId, true);
                return;
            }

            boolean released = releaseStockAndUpdateStatus(orderId, productId, quantity);
            sendStockReleasedResult(orderId, released);
        } catch (Exception e) {
            log.error("재고 복구 중 오류 발생: 주문 ID: {}, 오류: {}", orderId, e.getMessage(), e);
            sendStockReleasedResult(orderId, false);
        }
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void saveReservedOrdersToDatabase() {
        try {
            List<Map<String, Object>> reservedOrders = productCacheManager.getRecentReservedOrders(100);
            List<String> processedOrderIds = new ArrayList<>();

            for (Map<String, Object> order : reservedOrders) {
                String orderId = (String) order.get("orderId");
                UUID productId = UUID.fromString((String) order.get("productId"));
                int quantity = ((Integer) order.get("quantity"));

                int updatedRows = productService.decreaseStock(productId, quantity);

                if (updatedRows > 0) {
                    log.info("주문 {} 처리 완료: 제품 {}의 {} 단위가 데이터베이스에 저장되었습니다.", orderId, productId, quantity);
                    processedOrderIds.add(orderId);
                } else {
                    log.warn("주문 {} 처리 실패: 재고 부족으로 인해 처리할 수 없습니다.", orderId, productId);
                }
            }

            if (!processedOrderIds.isEmpty()) {
                productCacheManager.deleteOrders(processedOrderIds);
            }
        } catch (Exception e) {
            log.error("스케줄러 실행 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    private boolean isAlreadyReserved(String orderId) {
        String status = productCacheManager.getOrderStatus(orderId);
        return "RESERVED".equals(status);
    }

    private boolean isReservedOrder(String orderId) {
        String status = productCacheManager.getOrderStatus(orderId);
        return "RESERVED".equals(status);
    }

    private boolean reserveStockAndUpdateOrder(UUID productId, String orderId, int quantity) {
        boolean reserved = productCacheManager.reserveStock(productId, quantity);
        if (reserved) {
            productCacheManager.setOrderStatusAndQuantity(productId.toString(), orderId, "RESERVED", quantity);
        }
        return reserved;
    }

    private void handleReservationError(String orderId, UUID productId, int quantity, Exception e) {
        log.error("재고 예약 중 오류 발생: 주문 ID: {}, 오류: {}", orderId, e.getMessage(), e);
        releaseStockAndUpdateStatus(orderId, productId, quantity);
        sendStockCheckedResult(orderId, false);
    }

    private boolean releaseStockAndUpdateStatus(String orderId, UUID productId, int quantity) {
        try {
            if (isReservedOrder(orderId)) {
                productCacheManager.releaseStock(productId, quantity);
                productCacheManager.setOrderStatusAndQuantity(productId.toString(), orderId, "RELEASED", 0);
                log.info("재고 복구 완료: 주문 ID: {}, 제품 ID: {}, 수량: {}", orderId, productId, quantity);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("재고 복구 실패: 주문 ID: {}, 제품 ID: {}, 오류: {}", orderId, productId, e.getMessage(), e);
            return false;
        }
    }

    private void sendStockCheckedResult(String orderId, boolean isSuccess) {
        StockCheckedEvent event = StockCheckedEvent.newBuilder()
                .setOrderId(orderId)
                .setIsSuccess(isSuccess)
                .build();

        productProducer.sendStockCheckedEvent(event);
    }

    private void sendStockReleasedResult(String orderId, boolean isReleaseSuccess) {
        StockReleasedEvent event = StockReleasedEvent.newBuilder()
                .setOrderId(orderId)
                .setIsReleaseSuccess(isReleaseSuccess)
                .build();

        productProducer.sendStockReleased(event);
    }
}