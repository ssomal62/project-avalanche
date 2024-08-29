package site.leesoyeon.avalanche.order.infrastructure.saga.repository;

import site.leesoyeon.avalanche.order.infrastructure.saga.state.OrderSagaState;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface SagaStateRepository {
    void saveSagaState(UUID orderId, OrderSagaState state, Duration ttl);
    Optional<OrderSagaState> getSagaState(UUID orderId);
    boolean updateSagaStatus(UUID orderId, OrderSagaStatus newStatus, Duration ttl, int maxRetryCount);
}