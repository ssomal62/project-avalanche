package site.leesoyeon.probabilityrewardsystem.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 작업을 위한 유틸리티 클래스입니다.
 * 이 클래스는 Redis의 기본적인 CRUD 작업과 일반적인 데이터 구조 조작을 위한 메서드를 제공합니다.
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * RedisUtil 클래스의 생성자입니다.
     *
     * @param redisTemplate Redis 작업을 위한 RedisTemplate 객체
     */
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Redis에 키-값 쌍을 저장합니다.
     *
     * @param key 저장할 데이터의 키
     * @param value 저장할 데이터의 값
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Redis에 키-값 쌍을 저장하고 만료 시간을 설정합니다.
     *
     * @param key 저장할 데이터의 키
     * @param value 저장할 데이터의 값
     * @param timeout 만료 시간
     * @param unit 만료 시간의 단위
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 주어진 키에 해당하는 값을 Redis에서 조회합니다.
     *
     * @param key 조회할 데이터의 키
     * @return 조회된 데이터의 값, 키가 존재하지 않으면 null
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 주어진 키에 해당하는 데이터를 Redis에서 삭제합니다.
     *
     * @param key 삭제할 데이터의 키
     * @return 삭제 성공 시 true, 실패 시 false
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 주어진 키가 Redis에 존재하는지 확인합니다.
     *
     * @param key 존재 여부를 확인할 키
     * @return 키가 존재하면 true, 존재하지 않으면 false
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 주어진 키에 대한 만료 시간을 설정합니다.
     *
     * @param key 만료 시간을 설정할 키
     * @param timeout 만료 시간
     * @param unit 만료 시간의 단위
     * @return 설정 성공 시 true, 실패 시 false
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 주어진 키의 남은 만료 시간을 조회합니다.
     *
     * @param key 만료 시간을 조회할 키
     * @param unit 반환받을 시간의 단위
     * @return 남은 만료 시간, 영구적인 키의 경우 -1, 키가 존재하지 않으면 null
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * Redis List의 왼쪽(head)에 값을 추가합니다.
     *
     * @param key List의 키
     * @param value 추가할 값
     * @return 작업 완료 후 List의 길이
     */
    public Long listLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * Redis List의 왼쪽(head)에서 값을 추출합니다.
     *
     * @param key List의 키
     * @return 추출된 값, List가 비어있거나 키가 존재하지 않으면 null
     */
    public Object listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * Redis Hash에 필드와 값을 설정합니다.
     *
     * @param key Hash의 키
     * @param hashKey Hash 내의 필드 키
     * @param value 설정할 값
     */
    public void hashSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Redis Hash에서 지정된 필드의 값을 조회합니다.
     *
     * @param key Hash의 키
     * @param hashKey 조회할 Hash 내의 필드 키
     * @return 조회된 값, 필드가 존재하지 않으면 null
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
}