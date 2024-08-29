package site.leesoyeon.avalanche.order.infrastructure.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.infrastructure.exception.OrderSagaException;
import site.leesoyeon.avalanche.order.infrastructure.messaging.OrderSagaProducer;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandStatus;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandType;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;
import site.leesoyeon.avalanche.order.infrastructure.event.OrderCancelledEvent;
import site.leesoyeon.avalanche.order.infrastructure.event.OrderCompletedEvent;
import site.leesoyeon.avalanche.order.infrastructure.saga.repository.SagaStateRepository;
import site.leesoyeon.avalanche.order.infrastructure.saga.state.OrderSagaState;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaManager {

    /**************************************
     *        OrderSagaManager
     **************************************/

    private final OrderSagaProducer orderSagaProducer;
    private final SagaStateRepository sagaStateRepository;
    private final RedisTemplate<String, OrderSagaState> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    private static final String SAGA_KEY_PREFIX = "saga:";
    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final Duration LOCK_WAIT_TIME = Duration.ofSeconds(5);
    private static final Duration LOCK_LEASE_TIME = Duration.ofSeconds(10);
    private static final Duration ORDER_STATE_TTL = Duration.ofMinutes(10);
    private static final Duration SAGA_TIMEOUT = Duration.ofSeconds(20);
    private static final int MAX_RETRY_COUNT = 3;

    /*==================================
     *        사가 생명주기 관리
     ==================================*/

    public void startSaga(Order order, OrderRequest request) {
        OrderSagaState state = OrderSagaState.createInitialState(order, request);
        redisTemplate.opsForValue().set(SAGA_KEY_PREFIX + order.getOrderId(), state);

        orderSagaProducer.sendCheckStockCommand(order.getOrderId(), request.productInfo().productId().toString(), request.quantity());
        orderSagaProducer.sendApplyPointsCommand(order.getOrderId(), state.getUserId(), state.getAmount(), state.getActivityType(), state.getProductInfo().productName());
        orderSagaProducer.sendPrepareShippingCommand(order.getOrderId(), state.getShippingInfo());
    }

    public void handleCommandResponse(UUID orderId, CommandType commandType, boolean isSuccess) {
        try {
            executeWithLock(orderId, () -> {
                Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
                    updateStateIfNecessary(orderId, commandType, isSuccess, state);
                    log.info("Command response processed: orderId={}, commandType={}, isSuccess={}", orderId, commandType, isSuccess);
                    processStateAndDecideNextAction(orderId, commandType, state);
                });
                return null;
            });
        } catch (OrderSagaException e) {
            log.error("명령 응답 처리 중 오류 발생: orderId={}, commandType={}", orderId, commandType, e);
        }
    }

    private void updateStateIfNecessary(UUID orderId, CommandType commandType, boolean isSuccess, OrderSagaState state) {
        if (commandType != CommandType.TIMEOUT) {
            CommandStatus newStatus = isSuccess ? CommandStatus.SUCCESS : CommandStatus.FAILED;
            if (state.getStatus() == OrderSagaStatus.COMPENSATING) {
                state.updateCompensationStatus(commandType, newStatus);
            } else {
                state.updateCommandStatus(commandType, newStatus);
            }
            saveSagaState(orderId, state);
        }
    }

    private void processStateAndDecideNextAction(UUID orderId, CommandType commandType, OrderSagaState state) {
        if (state.allCommandsResponded() || commandType == CommandType.TIMEOUT) {
            if (state.hasFailedCommands()) {
                handleFailedCommands(orderId, state);
            } else {
                completeSaga(orderId);
            }
        }
    }

    /*==================================
     *        실패 및 보상 처리
     ==================================*/

    private void handleFailedCommands(UUID orderId, OrderSagaState state) {
        if (state.allCompensationsCompleted()) {
            cancelOrder(orderId);
        } else {
            handleFailureAndCompensation(orderId);
        }
    }

    private void handleFailureAndCompensation(UUID orderId) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            state.updateStatus(OrderSagaStatus.COMPENSATING);
            saveSagaState(orderId, state);

            for (CommandType commandType : CommandType.values()) {
                if (commandType == CommandType.TIMEOUT) continue;
                if (state.getCommandStatuses().get(commandType) == CommandStatus.SUCCESS &&
                        !state.getCompensationStatuses().containsKey(commandType)) {
                    sendCompensationCommand(orderId, commandType);
                }
            }
        });
    }

    private void sendCompensationCommand(UUID orderId, CommandType commandType) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            switch (commandType) {
                case CHECK_STOCK:
                    orderSagaProducer.sendReleaseStockCommand(orderId, state.getProductInfo().productId(), state.getQuantity());
                    break;
                case APPLY_POINTS:
                    if (state.getPointId() != null) {
                        orderSagaProducer.sendRefundPointCommand(orderId, state.getPointId(), state.getAmount());
                    }
                    break;
                case PREPARE_SHIPPING:
                    if (state.getShippingId() != null) {
                        orderSagaProducer.sendCancelShippingCommand(orderId, state.getShippingId());
                    }
                    break;
            }
            state.updateCompensationStatus(commandType, CommandStatus.PENDING);
            saveSagaState(orderId, state);
        });
    }

    /*==================================
     *          사가 상태 변경
     ==================================*/

    void completeSaga(UUID orderId) {
        if (updateSagaStatus(orderId, OrderSagaStatus.COMPLETED)) {
            log.info("Saga completed successfully: orderId={}", orderId);
            eventPublisher.publishEvent(new OrderCompletedEvent(orderId));
        } else {
            log.error("Failed to complete saga: orderId={}", orderId);
        }
    }

    void cancelOrder(UUID orderId) {
        if (updateSagaStatus(orderId, OrderSagaStatus.CANCELLED)) {
            log.info("주문 취소 시작: orderId={}", orderId);
            eventPublisher.publishEvent(new OrderCancelledEvent(orderId));
        } else {
            log.error("Failed to cancel order: orderId={}", orderId);
        }
    }

    /*==================================
     *         명령 응답 핸들러
     ==================================*/

    public void handleStockChecked(UUID orderId, boolean reservationSuccess) {
        handleCommandResponse(orderId, CommandType.CHECK_STOCK, reservationSuccess);
    }

    public void handlePointApplied(UUID orderId, UUID pointId, boolean isSuccess) {
        if (isSuccess && pointId != null) {
            try {
                executeWithLock(orderId, () -> {
                    Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
                        state.updatePointId(pointId);
                        saveSagaState(orderId, state);
                        log.info("포인트 ID 업데이트 완료: oderId={}, pointId={}", orderId, pointId);
                    });
                    return null;
                });
            } catch (OrderSagaException e) {
                log.error("명령 응답 처리 중 오류 발생: orderId={}, commandType={}", orderId, CommandType.APPLY_POINTS, e);
            }
        }
        handleCommandResponse(orderId, CommandType.APPLY_POINTS, isSuccess);
    }

    public void handleShippingPrepared(UUID orderId, UUID shippingId, boolean isSuccess) {
        if (isSuccess && shippingId != null) {
            try {
                executeWithLock(orderId, () -> {
                Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
                    state.updateShippingId(shippingId);
                    saveSagaState(orderId, state);
                    log.info("배송 ID 업데이트 완료: oderId={}, shippingId={}", orderId, shippingId);
                });
                    return null;
                });
            } catch (OrderSagaException e) {
                log.error("명령 응답 처리 중 오류 발생: orderId={}, commandType={}", orderId, CommandType.APPLY_POINTS, e);
            }
        }
        handleCommandResponse(orderId, CommandType.PREPARE_SHIPPING, isSuccess);
    }

    /*==================================
     *      보상 트랜잭션 응답 핸들러
     ==================================*/

    public void handleStockReleased(UUID orderId, boolean isSuccess) {
        handleCommandResponse(orderId, CommandType.CHECK_STOCK, isSuccess);
    }

    public void handlePointsRefunded(UUID orderId, boolean isSuccess) {
        handleCommandResponse(orderId, CommandType.APPLY_POINTS, isSuccess);
    }

    public void handleShippingCancelled(UUID orderId, boolean isSuccess) {
        handleCommandResponse(orderId, CommandType.PREPARE_SHIPPING, isSuccess);
    }

    /*==================================
     *           타임아웃 처리
     ==================================*/

    public void initiateCompensationForTimeout(UUID orderId) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            log.info("타임아웃 발생: orderId={}", orderId);

            for (CommandType commandType : CommandType.values()) {
                if (commandType == CommandType.TIMEOUT) continue;

                CommandStatus currentStatus = state.getCommandStatuses().get(commandType);
                if (currentStatus == null || currentStatus == CommandStatus.PENDING) {
                    state.updateCommandStatus(commandType, CommandStatus.FAILED);

                    log.info("CommandType {} 상태를 FAILED로 업데이트: orderId={}, 이전 상태={}",
                            commandType, orderId, currentStatus);
                }
            }

            if (state.getCommandStatuses().get(CommandType.TIMEOUT) != CommandStatus.FAILED) {
                state.updateCommandStatus(CommandType.TIMEOUT, CommandStatus.FAILED);
            }

            saveSagaState(orderId, state);
            log.info("타임아웃으로 인한 상태 업데이트 완료: orderId={}, 변경된 상태들={}", orderId, state.getCommandStatuses());
            handleCommandResponse(orderId, CommandType.TIMEOUT, false);
        });
    }

    /*==================================
     *          유틸리티 메서드
     ==================================*/

//    private CompletableFuture<Void> executeWithAsyncLock(UUID orderId, Supplier<CompletableFuture<Void>> action) {
//        String lockKey = LOCK_KEY_PREFIX + orderId;
//        RLock lock = redissonClient.getLock(lockKey);
//
//        return lock.tryLockAsync(LOCK_WAIT_TIME.toMillis(), LOCK_LEASE_TIME.toMillis(), TimeUnit.MILLISECONDS)
//                .thenCompose(locked -> {
//                    if (locked) {
//                        return action.get()
//                                .whenComplete((result, ex) -> lock.unlockAsync())
//                                .exceptionally(ex -> {
//                                    log.error("Lock 내부 작업 실행 중 오류 발생: orderId={}", orderId, ex);
//                                    return null;
//                                });
//                    } else {
//                        return CompletableFuture.failedFuture(new OrderSagaException("Lock 획득 실패: orderId=" + orderId));
//                    }
//                })
//                .exceptionally(ex -> {
//                    log.error("Lock 획득 중 오류 발생: orderId={}", orderId, ex);
//                    return null;
//                }).toCompletableFuture();
//    }}

    <T> void executeWithLock(UUID orderId, Supplier<T> action) {
        String lockKey = LOCK_KEY_PREFIX + orderId;
        RLock lock = redissonClient.getLock(lockKey);

        for (int attempt = 0; attempt < MAX_RETRY_COUNT; attempt++) {
            if (tryLockAndExecute(lock, action)) {
                return;
            }
            waitBeforeRetry(orderId, attempt);
        }
        throw new OrderSagaException("최대 재시도 횟수 초과: orderId=" + orderId);
    }

    private <T> boolean tryLockAndExecute(RLock lock, Supplier<T> action) {
        try {
            if (lock.tryLock(LOCK_WAIT_TIME.toMillis(), LOCK_LEASE_TIME.toMillis(), TimeUnit.MILLISECONDS)) {
                try {
                    action.get();
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderSagaException("락 획득 중 인터럽트 발생", e);
        }
        return false;
    }

    private void waitBeforeRetry(UUID orderId, int attempt) {
        long waitTimeMillis = LOCK_WAIT_TIME.toMillis() * (long) Math.pow(2, attempt);
        log.warn("락 획득 실패 (시도 {}/{}): orderId={}", attempt + 1, MAX_RETRY_COUNT, orderId);
        try {
            Thread.sleep(waitTimeMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderSagaException("대기 중 인터럽트 발생", e);
        }
    }

    void saveSagaState(UUID orderId, OrderSagaState state) {
        sagaStateRepository.saveSagaState(orderId, state, ORDER_STATE_TTL);
    }

    private Optional<OrderSagaState> getValidSagaState(UUID orderId) {
        return sagaStateRepository.getSagaState(orderId);
    }

    private boolean updateSagaStatus(UUID orderId, OrderSagaStatus newStatus) {
        return sagaStateRepository.updateSagaStatus(orderId, newStatus, ORDER_STATE_TTL, MAX_RETRY_COUNT);
    }
}