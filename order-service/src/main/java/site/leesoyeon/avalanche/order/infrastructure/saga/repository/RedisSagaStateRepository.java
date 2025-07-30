package site.leesoyeon.avalanche.order.infrastructure.saga.repository;

import jakarta.annotation.Nonnull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import site.leesoyeon.avalanche.order.infrastructure.exception.OrderSagaException;
import site.leesoyeon.avalanche.order.infrastructure.saga.state.OrderSagaState;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RedisSagaStateRepository implements SagaStateRepository {

    private final RedisTemplate<String, OrderSagaState> redisTemplate;

    public RedisSagaStateRepository(RedisTemplate<String, OrderSagaState> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveSagaState(UUID orderId, OrderSagaState state, Duration ttl) {
        redisTemplate.opsForValue().set("saga:" + orderId, state, ttl);
    }

    @Override
    public Optional<OrderSagaState> getSagaState(UUID orderId) {
        OrderSagaState state = redisTemplate.opsForValue().get("saga:" + orderId);
        return Optional.ofNullable(state);
    }

    @Override
    public boolean updateSagaStatus(UUID orderId, OrderSagaStatus newStatus, Duration ttl, int maxRetryCount) {
        String key = "saga:" + orderId;
        for (int attempt = 0; attempt < maxRetryCount; attempt++) {
            Boolean result = redisTemplate.execute(new SessionCallback<Boolean>() {
                @Override
                @SuppressWarnings("unchecked")
                public Boolean execute(@Nonnull RedisOperations operations) throws DataAccessException {
                    operations.watch(key);
                    OrderSagaState state = (OrderSagaState) operations.opsForValue().get(key);
                    if (state == null) {
                        return false;
                    }
                    operations.multi();
                    state.updateStatus(newStatus);
                    operations.opsForValue().set(key, state, ttl);
                    List<Object> results = operations.exec();
                    return !results.isEmpty();
                }
            });

            if (Boolean.TRUE.equals(result)) {
                return true;
            }

            try {
                Thread.sleep(100 * (long) Math.pow(2, attempt)); // 지수 백오프
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new OrderSagaException("Status update interrupted", e);
            }
        }
        return false;
    }
}