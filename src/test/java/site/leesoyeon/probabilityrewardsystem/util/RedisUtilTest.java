package site.leesoyeon.probabilityrewardsystem.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String TEST_KEY = "testKey";
    private final String TEST_VALUE = "testValue";

    private static final Logger logger = LoggerFactory.getLogger(RedisUtilTest.class);

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
        redisUtil.set(TEST_KEY, TEST_VALUE);
        assertEquals(TEST_VALUE, redisUtil.get(TEST_KEY));
    }

    @Test
    @DisplayName("만료 시간을 설정한 set 및 get 테스트")
    void testSetWithExpiration() throws InterruptedException {
        redisUtil.set(TEST_KEY, TEST_VALUE, 1, TimeUnit.SECONDS);
        assertEquals(TEST_VALUE, redisUtil.get(TEST_KEY));

        Thread.sleep(1100); // 만료 시간보다 조금 더 기다립니다.
        assertNull(redisUtil.get(TEST_KEY));
    }

    @Test
    @DisplayName("삭제 기능 테스트")
    void testDelete() {
        redisUtil.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtil.delete(TEST_KEY));
        assertNull(redisUtil.get(TEST_KEY));
    }

    @Test
    @DisplayName("키 존재 여부 확인 테스트")
    void testHasKey() {
        redisUtil.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtil.hasKey(TEST_KEY));
        redisUtil.delete(TEST_KEY);
        assertFalse(redisUtil.hasKey(TEST_KEY));
    }

    @Test
    @DisplayName("만료 시간 설정 및 만료 후 확인 테스트")
    void testExpire() throws InterruptedException {
        redisUtil.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtil.expire(TEST_KEY, 1, TimeUnit.SECONDS));

        Thread.sleep(1100);
        assertNull(redisUtil.get(TEST_KEY));
    }

    @Test
    @DisplayName("만료 시간 조회 테스트")
    void testGetExpire() {
        redisUtil.set(TEST_KEY, TEST_VALUE, 10, TimeUnit.SECONDS);
        Long expireTime = redisUtil.getExpire(TEST_KEY, TimeUnit.SECONDS);
        assertTrue(expireTime > 0 && expireTime <= 10);
    }

    @Test
    @DisplayName("리스트 연산 테스트")
    void testListOperations() {
        redisUtil.listLeftPush(TEST_KEY, "value1");
        redisUtil.listLeftPush(TEST_KEY, "value2");

        assertEquals("value2", redisUtil.listLeftPop(TEST_KEY));
        assertEquals("value1", redisUtil.listLeftPop(TEST_KEY));
        assertNull(redisUtil.listLeftPop(TEST_KEY));
    }

    @Test
    @DisplayName("해시 연산 테스트")
    void testHashOperations() {
        redisUtil.hashSet(TEST_KEY, "field1", "value1");
        redisUtil.hashSet(TEST_KEY, "field2", "value2");

        assertEquals("value1", redisUtil.hashGet(TEST_KEY, "field1"));
        assertEquals("value2", redisUtil.hashGet(TEST_KEY, "field2"));
        assertNull(redisUtil.hashGet(TEST_KEY, "field3"));
    }
}
