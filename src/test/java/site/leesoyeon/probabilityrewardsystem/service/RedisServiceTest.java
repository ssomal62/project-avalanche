package site.leesoyeon.probabilityrewardsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String TEST_KEY = "testKey";
    private final String TEST_VALUE = "testValue";

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceTest.class);

    @BeforeEach
    void setUp() {
        try {
            redisTemplate.delete(TEST_KEY);
        } catch (Exception e) {
            logger.error("Redis 연결에 실패했습니다: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("단순 set 및 get 테스트")
    void testSetAndGet() {
        redisService.set(TEST_KEY, TEST_VALUE);
        assertEquals(TEST_VALUE, redisService.get(TEST_KEY));
    }

    @Test
    @DisplayName("만료 시간을 설정한 set 및 get 테스트")
    void testSetWithExpiration() throws InterruptedException {
        redisService.set(TEST_KEY, TEST_VALUE, 1, TimeUnit.SECONDS);
        assertEquals(TEST_VALUE, redisService.get(TEST_KEY));

        Thread.sleep(1100); // 만료 시간보다 조금 더 기다립니다.
        assertNull(redisService.get(TEST_KEY));
    }

    @Test
    @DisplayName("삭제 기능 테스트")
    void testDelete() {
        redisService.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisService.delete(TEST_KEY));
        assertNull(redisService.get(TEST_KEY));
    }

    @Test
    @DisplayName("키 존재 여부 확인 테스트")
    void testHasKey() {
        redisService.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisService.hasKey(TEST_KEY));
        redisService.delete(TEST_KEY);
        assertFalse(redisService.hasKey(TEST_KEY));
    }
}
