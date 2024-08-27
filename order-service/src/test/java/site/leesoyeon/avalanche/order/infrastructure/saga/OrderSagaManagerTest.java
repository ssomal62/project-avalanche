package site.leesoyeon.avalanche.order.infrastructure.saga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import site.leesoyeon.avalanche.order.infrastructure.messaging.OrderSagaProducer;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderSagaManagerTest {

    @Mock
    private OrderSagaProducer orderSagaProducer;

    @Mock
    private RedisTemplate<String, OrderSagaState> redisTemplate;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RedissonClient redissonClient;

    private OrderSagaManager orderSagaManager;

    @BeforeEach
    void setUp() {
        orderSagaManager = new OrderSagaManager(orderSagaProducer, redisTemplate, eventPublisher, redissonClient);
    }

    @Test
    void startSagaTest() {

    }

}