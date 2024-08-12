package site.leesoyeon.probabilityrewardsystem.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.auth.exception.AuthException;
import site.leesoyeon.probabilityrewardsystem.jwt.config.JwtProperties;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static site.leesoyeon.probabilityrewardsystem.common.Constants.BLACK_LIST_KEY_PREFIX;
import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.BLACKLISTED_TOKEN;
import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.REFRESH_TOKEN_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public void saveRefreshToken(String email, String clientId, String refreshToken) {
        String key = "refresh_token:" + email + ":" + clientId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                jwtProperties.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );
    }

    public String getRefreshToken(String email, String clientId) {
        String key = "refresh_token:" + email + ":" + clientId;
        String token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            throw new AuthException(REFRESH_TOKEN_NOT_FOUND);
        }
        return token;
    }

    public void deleteRefreshToken(String email, String clientId) {
        String key = "refresh_token:" + email + ":" + clientId;
        redisTemplate.delete(key);
    }

    public void checkIfTokenInvalidated(String token) {
        Boolean isInvalidated = redisTemplate.hasKey(BLACK_LIST_KEY_PREFIX + token);
        if (Boolean.TRUE.equals(isInvalidated)) {
            throw new AuthException(BLACKLISTED_TOKEN);
        }
    }

    public List<String> getAllRefreshTokens(String email) {
        // 사용자의 모든 리프레시 토큰을 가져옴
        Set<String> keys = redisTemplate.keys("refresh_token:" + email + ":*");
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());
    }

    public void deleteAllRefreshTokens(String email) {
        Set<String> keys = redisTemplate.keys("refresh_token:" + email + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public String validateAndReturnRefreshToken(String email, String clientId) {
        String refreshToken = getRefreshToken(email, clientId);
        if (refreshToken == null) {
            throw new AuthException(REFRESH_TOKEN_NOT_FOUND);
        }
        return refreshToken;
    }

    public void invalidateToken(String token) {
        redisTemplate.opsForValue().set(
                BLACK_LIST_KEY_PREFIX + token,
                "true",
                jwtProperties.getAccessTokenValidity(),
                TimeUnit.MILLISECONDS
        );
    }
}
