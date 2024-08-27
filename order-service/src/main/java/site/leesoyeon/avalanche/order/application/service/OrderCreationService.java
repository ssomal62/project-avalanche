package site.leesoyeon.avalanche.order.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.order.application.util.OrderMapper;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.infrastructure.saga.OrderSagaManager;
import site.leesoyeon.avalanche.order.infrastructure.saga.OrderSagaState;
import site.leesoyeon.avalanche.order.infrastructure.saga.event.OrderCancelledEvent;
import site.leesoyeon.avalanche.order.infrastructure.saga.event.OrderCompletedEvent;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;
import site.leesoyeon.avalanche.order.presentation.dto.OrderResponse;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreationService {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderSagaManager orderSagaManager;
    private final RedisOperations<String, OrderSagaState> redisOperations;

    private static final int MAX_RETRIES = 30;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(1);
    private static final Duration SAGA_TIMEOUT = Duration.ofSeconds(60);

    public CompletableFuture<OrderResponse> createAndProcessOrder(OrderRequest request) {
        return createInitialOrder(request)
                .thenCompose(order ->
                        CompletableFuture.runAsync(() -> orderSagaManager.startSaga(order, request))
                                .thenCompose(v -> waitForOrderCompletion(order.getOrderId()))
                )
                .exceptionally(ex -> {
                    log.error("주문 처리 중 오류 발생", ex);
                    throw new CompletionException(ex);
                });
    }

    private CompletableFuture<OrderResponse> waitForOrderCompletion(UUID orderId) {
        return CompletableFuture.supplyAsync(() -> {
                    for (int i = 0; i < MAX_RETRIES; i++) {
                        try {
                            OrderSagaState state = redisOperations.opsForValue().get("saga:" + orderId);
                            if (state == null) {
                                throw new IllegalStateException("주문 상태를 찾을 수 없습니다");
                            }
                            if (state.getStatus().isTerminal()) {
                                return createOrderResponse(state);
                            }
                            Thread.sleep(RETRY_DELAY);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new CompletionException(e);
                        }
                    }
                    throw new IllegalStateException("주문이 완료되지 않았습니다");
                }).orTimeout(SAGA_TIMEOUT.toSeconds(), TimeUnit.MINUTES)
                .exceptionally(ex -> {
                    log.error("주문 완료 대기 중 오류 발생: orderId={}", orderId, ex);
                    orderSagaManager.initiateCompensationForTimeout(orderId);
                    throw new CompletionException(new IllegalStateException("주문 완료 실패", ex));
                });
    }

    @Async
    public CompletableFuture<Order> createInitialOrder(OrderRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = orderMapper.toEntity(request);
            order.updateStatus(OrderStatus.CREATED);
            return orderService.saveOrder(order);
        });
    }

    private OrderResponse createOrderResponse(OrderSagaState state) {
        return OrderResponse.builder()
                .orderId(state.getOrderId())
                .userId(state.getUserId())
                .quantity(state.getQuantity())
                .finalAmount(state.getAmount())
                .productInfo(state.getProductInfo())
                .shippingInfo(orderMapper.toShippingInfoDto(state.getShippingInfo()))
                .status(OrderStatus.COMPLETED)
                .build();
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        try {
            orderService.deleteOrderById(event.orderId());
            log.info("주문 취소처리가 완료되었습니다. orderId: {}", event.orderId());
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("주문 취소 중 충돌 발생. 이미 처리되었을 수 있음. orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("주문 취소 중 오류 발생. orderId: {}", event.orderId(), e);
        }
    }

    @EventListener
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        Order order = orderService.findOrderById(event.orderId());
        order.updateStatus(OrderStatus.COMPLETED);
        orderService.saveOrder(order);
        log.info("주문 완료처리가 완료되었습니다. orderId: {}", event.orderId());
    }

}
