package site.leesoyeon.avalanche.order.infrastructure.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.infrastructure.messaging.OrderSagaProducer;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandStatus;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandType;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;
import site.leesoyeon.avalanche.order.infrastructure.saga.event.OrderCancelledEvent;
import site.leesoyeon.avalanche.order.infrastructure.saga.event.OrderCompletedEvent;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaManager {

    /**************************************
     *        OrderSagaManager
     **************************************/

    private final OrderSagaProducer orderSagaProducer;
    private final RedisTemplate<String, OrderSagaState> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    private static final String SAGA_KEY_PREFIX = "saga:";
    private static final Duration LOCK_WAIT_TIME = Duration.ofSeconds(5);
    private static final Duration LOCK_LEASE_TIME = Duration.ofSeconds(10);
    private static final Duration ORDER_STATE_TTL = Duration.ofMinutes(10);
    private static final Duration SAGA_TIMEOUT = Duration.ofSeconds(20);

    /*==================================
     *        사가 생명주기 관리
     ==================================*/

    public void startSaga(Order order, OrderRequest request) {
        OrderSagaState state = OrderSagaState.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .quantity(request.quantity())
                .amount(request.amount())
                .activityType(request.activityType())
                .productInfo(request.productInfo())
                .shippingInfo(request.shippingInfo())
                .status(OrderSagaStatus.STARTED)
                .build();

        redisTemplate.opsForValue().set(SAGA_KEY_PREFIX + order.getOrderId(), state);

        orderSagaProducer.sendCheckStockCommand(order.getOrderId(), request.productInfo().productId().toString(), request.quantity());
        orderSagaProducer.sendApplyPointsCommand(order.getOrderId(), state.getUserId(), state.getAmount(), state.getActivityType(), state.getProductInfo().productName());
        orderSagaProducer.sendPrepareShippingCommand(order.getOrderId(), state.getShippingInfo());
    }

    public void handleCommandResponse(UUID orderId, CommandType commandType, boolean isSuccess) {
        String lockKey = "lock:" + orderId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME.toMillis(), LOCK_LEASE_TIME.toMillis(), TimeUnit.MILLISECONDS)) {
                try {
                    processCommandResponse(orderId, commandType, isSuccess);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("락 획득 실패: orderId={}", orderId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("락 획득 중 인터럽트 발생: orderId={}", orderId, e);
        } catch (Exception e) {
            log.error("명령 응답 처리 중 오류 발생: orderId={}, commandType={}", orderId, commandType, e);
        }
    }

    private void processCommandResponse(UUID orderId, CommandType commandType, boolean isSuccess) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            if (commandType != CommandType.TIMEOUT) {
                if (state.getStatus() == OrderSagaStatus.COMPENSATING) {
                    state.updateCompensationStatus(commandType, isSuccess ? CommandStatus.SUCCESS : CommandStatus.FAILED);
                } else {
                    state.updateCommandStatus(commandType, isSuccess ? CommandStatus.SUCCESS : CommandStatus.FAILED);
                }
                saveSagaState(orderId, state);
            }

            log.info("Command response processed: orderId={}, commandType={}, isSuccess={}", orderId, commandType, isSuccess);

            if (state.allCommandsResponded() || commandType == CommandType.TIMEOUT) {
                if (state.hasFailedCommands()) {
                    if (state.allCompensationsCompleted()) {
                        cancelOrder(orderId);
                    } else {
                        handleFailureAndCompensation(orderId);
                    }
                } else {
                    completeSaga(orderId);
                }
            }
        });
    }

    /*==================================
     *        실패 및 보상 처리
     ==================================*/

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

    private void cancelOrder(UUID orderId) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            state.updateStatus(OrderSagaStatus.CANCELLED);
            saveSagaState(orderId, state);
            log.info("주문 취소 시작: orderId={}", orderId);
            eventPublisher.publishEvent(new OrderCancelledEvent(orderId));
        });
    }

    private void completeSaga(UUID orderId) {
        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
            state.updateStatus(OrderSagaStatus.COMPLETED);
            saveSagaState(orderId, state);
            log.info("Saga completed successfully: orderId={}", orderId);
            eventPublisher.publishEvent(new OrderCompletedEvent(orderId));
        });
    }

    /*==================================
     *         명령 응답 핸들러
     ==================================*/

    public void handleStockChecked(UUID orderId, boolean reservationSuccess) {
        handleCommandResponse(orderId, CommandType.CHECK_STOCK, reservationSuccess);
    }

    public void handlePointApplied(UUID orderId, UUID pointId, boolean isSuccess) {
        if (isSuccess && pointId != null) {
            String lockKey = "lock:" + orderId;
            RLock lock = redissonClient.getLock(lockKey);

            try {
                if (lock.tryLock(LOCK_WAIT_TIME.toMillis(), LOCK_LEASE_TIME.toMillis(), TimeUnit.MILLISECONDS)) {
                    try {
                        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
                            state.updatePointId(pointId);
                            saveSagaState(orderId, state);
                            log.info("포인트 ID 업데이트 완료: orderId={}, pointId={}", orderId, pointId);
                        });
                    } finally {
                        lock.unlock();
                    }
                } else {
                    log.warn("포인트 ID 업데이트를 위한 락 획득 실패: orderId={}", orderId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("포인트 ID 업데이트를 위한 락 획득 중 인터럽트 발생: orderId={}", orderId, e);
            }
        }
        handleCommandResponse(orderId, CommandType.APPLY_POINTS, isSuccess);
    }

    public void handleShippingPrepared(UUID orderId, UUID shippingId, boolean isSuccess) {
        if (isSuccess && shippingId != null) {
            String lockKey = "lock:" + orderId;
            RLock lock = redissonClient.getLock(lockKey);

            try {
                if (lock.tryLock(LOCK_WAIT_TIME.toMillis(), LOCK_LEASE_TIME.toMillis(), TimeUnit.MILLISECONDS)) {
                    try {
                        Objects.requireNonNull(getValidSagaState(orderId)).ifPresent(state -> {
                            state.updateShippingId(shippingId);
                            saveSagaState(orderId, state);
                            log.info("배송 ID 업데이트 완료: orderId={}, shippingId={}", orderId, shippingId);
                        });
                    } finally {
                        lock.unlock();
                    }
                } else {
                    log.warn("배송 ID 업데이트를 위한 락 획득 실패: orderId={}", orderId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("배송 ID 업데이트를 위한 락 획득 중 인터럽트 발생: orderId={}", orderId, e);
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

            boolean stateChanged = false;

            for (CommandType commandType : CommandType.values()) {
                if (commandType == CommandType.TIMEOUT) continue;

                CommandStatus currentStatus = state.getCommandStatuses().get(commandType);
                if (currentStatus == null || currentStatus == CommandStatus.PENDING) {
                    state.updateCommandStatus(commandType, CommandStatus.FAILED);
                    stateChanged = true;
                    log.info("CommandType {} 상태를 FAILED로 업데이트: orderId={}, 이전 상태={}",
                            commandType, orderId, currentStatus);
                }
            }

            if (state.getCommandStatuses().get(CommandType.TIMEOUT) != CommandStatus.FAILED) {
                state.updateCommandStatus(CommandType.TIMEOUT, CommandStatus.FAILED);
                stateChanged = true;
            }

            if (stateChanged) {
                saveSagaState(orderId, state);
                log.info("타임아웃으로 인한 상태 업데이트 완료: orderId={}, 변경된 상태들={}", orderId, state.getCommandStatuses());
                handleCommandResponse(orderId, CommandType.TIMEOUT, false);
            } else {
                log.info("타임아웃 처리 중 상태 변경 없음: orderId={}", orderId);
            }
        });
    }

    /*==================================
     *          유틸리티 메서드
     ==================================*/

    private Optional<OrderSagaState> getValidSagaState(UUID orderId) {
        OrderSagaState state = redisTemplate.opsForValue().get(SAGA_KEY_PREFIX + orderId);
        if (state == null) {
            log.warn("Saga 상태를 찾을 수 없습니다 : orderId={}", orderId);
        }

        assert state != null;
        return Optional.of(state);
    }

    private void saveSagaState(UUID orderId, OrderSagaState state) {
        redisTemplate.opsForValue().set(SAGA_KEY_PREFIX + orderId, state, ORDER_STATE_TTL);
    }
}