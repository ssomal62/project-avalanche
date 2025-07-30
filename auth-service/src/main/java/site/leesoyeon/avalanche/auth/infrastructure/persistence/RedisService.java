package site.leesoyeon.avalanche.auth.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import site.leesoyeon.avalanche.auth.application.util.Constants;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis에 키-값 쌍을 저장합니다.
     *
     * @param key   저장할 데이터의 키
     * @param value 저장할 데이터의 값
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Redis에 키-값 쌍을 저장하고 만료 시간을 설정합니다.
     *
     * @param key     저장할 데이터의 키
     * @param value   저장할 데이터의 값
     * @param timeout 만료 시간
     * @param unit    만료 시간의 단위
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * Redis에 데이터 저장 및 만료 시간 설정
     *
     * @param key      저장할 데이터의 Key
     * @param value    저장할 데이터
     * @param duration 만료 시간
     */
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
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
     * Redis에 저장된 데이터(Key:Value) 가져오기
     *
     * @param key 가져올 데이터의 Key
     * @return 데이터
     */
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * 주어진 패턴에 맞는 모든 키를 Redis에서 검색하여 반환합니다.
     *
     * @param pattern 검색할 키 패턴 (예: "refreshToken:email:*")
     * @return 패턴에 맞는 모든 키의 집합
     */
    public Set<String> getKeys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            return new HashSet<>(); // 에러 발생 시 빈 집합 반환
        }
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
     * 이메일 인증을 위한 토큰을 Redis에 저장합니다.
     * 이 토큰은 일정 시간 후 만료되도록 설정됩니다.
     *
     * @param email   인증할 사용자의 이메일 주소
     * @param token   인증에 사용할 토큰 값
     * @param timeout 토큰의 만료 시간
     * @param unit    만료 시간의 단위
     */
    public void saveEmailVerificationToken(String email, String token, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(email, token, timeout, unit);
    }

    /**
     * 이메일에 대한 인증 토큰을 Redis에서 조회합니다.
     *
     * @param email 인증할 사용자의 이메일 주소
     * @return 조회된 인증 토큰, 토큰이 없거나 만료된 경우 null
     */
    public String getEmailVerificationToken(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    /**
     * 이메일 인증이 완료되었거나 만료된 토큰을 삭제합니다.
     *
     * @param token 인증이 완료된 사용자의 이메일 주소
     */
    public void deleteEmailVerificationToken(String token) {
        redisTemplate.delete(token);
    }

    /**
     * JWT 토큰을 블랙리스트에 추가합니다.
     *
     * @param token          블랙리스트에 추가할 토큰
     * @param expirationTime 토큰의 만료 시간 (초 단위)
     */
    public void addToBlacklist(String token, long expirationTime) {
        String key = Constants.BLACK_LIST_KEY_PREFIX + token;
        set(key, "true", expirationTime, TimeUnit.SECONDS);
    }

    /**
     * JWT 토큰이 블랙리스트에 있는지 확인합니다.
     *
     * @param token 확인할 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isBlacklisted(String token) {
        String key = Constants.BLACK_LIST_KEY_PREFIX + token;
        return hasKey(key);
    }

}