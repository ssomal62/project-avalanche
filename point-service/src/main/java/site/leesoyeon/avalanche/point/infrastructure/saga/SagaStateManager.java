package site.leesoyeon.avalanche.point.infrastructure.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaStateManager {

    private final RedisTemplate<String, OrderSagaState> redisTemplate;
    private static final String SAGA_KEY_PREFIX = "saga:";

    private Optional<OrderSagaState> getSagaStateIfValid(String orderId) {
        OrderSagaState state = redisTemplate.opsForValue().get(SAGA_KEY_PREFIX + orderId);
        if (state == null) {
            log.warn("Saga 상태를 찾을 수 없습니다 : orderId={}", orderId);
            return Optional.empty();
        }

        if (state.getStatus().equals("FAILED") || state.getStatus().equals("CANCELLED")) {
            log.warn("사가 상태가 {}이므로 커맨드를 무시합니다. orderId={}", state.getStatus(), orderId);
            return Optional.empty();
        }

        return Optional.of(state);
    }

    public void processCommandIfSagaStateValid(String orderId, Runnable onValidSagaState, Acknowledgment ack) {
        Optional<OrderSagaState> sagaStateOpt = getSagaStateIfValid(orderId);

        if (sagaStateOpt.isEmpty()) {
            log.warn("유효한 사가 상태를 찾을 수 없으므로 메시지를 커밋하고 종료합니다. orderId={}", orderId);
            ack.acknowledge();
            return;
        }

        onValidSagaState.run();
        ack.acknowledge();
    }
}