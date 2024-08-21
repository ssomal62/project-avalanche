package site.leesoyeon.avalanche.auth.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static site.leesoyeon.avalanche.auth.application.util.Constants.BLACK_LIST_KEY_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * JWT 서명에 사용할 키를 생성합니다.
     * @return SecretKey 객체
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 생성합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @param role 사용자 역할
     * @return TokenPair 객체 (액세스 토큰과 리프레시 토큰 포함)
     */
    public TokenPair createTokenPair(UUID userId, String clientId, String role) {
        String accessToken = createAccessToken(userId, clientId, role);
        String refreshToken = createRefreshToken(userId, clientId);

        storeTokens(userId, clientId, role, accessToken, refreshToken);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 액세스 토큰을 생성합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @param role 사용자 역할
     * @return 생성된 액세스 토큰
     */
    private String createAccessToken(UUID userId, String clientId, String role) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("clientId", clientId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @return 생성된 리프레시 토큰
     */
    private String createRefreshToken(UUID userId, String clientId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(refreshTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("clientId", clientId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 생성된 토큰을 Redis에 저장합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @param role 사용자 역할
     * @param accessToken 액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    private void storeTokens(UUID userId, String clientId, String role, String accessToken, String refreshToken) {
        String userKey = "user:" + userId + ":" + clientId;

        redisTemplate.opsForValue().set("token:" + accessToken, userId + ":" + clientId + ":" + role, accessTokenExpiration, TimeUnit.MILLISECONDS);

        redisTemplate.opsForHash().put(userKey, "refreshToken", refreshToken);
        redisTemplate.expire(userKey, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 주어진 userId와 clientId에 해당하는 리프레시 토큰을 조회합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @return 저장된 리프레시 토큰, 없으면 null
     */
    public String getRefreshToken(UUID userId, String clientId) {
        String userKey = "user:" + userId + ":" + clientId;
        Object storedRefreshToken = redisTemplate.opsForHash().get(userKey, "refreshToken");
        return storedRefreshToken != null ? storedRefreshToken.toString() : null;
    }

    /**
     * 액세스 토큰의 유효성을 검증합니다.
     * @param accessToken 액세스 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(accessToken);
            return !isTokenBlacklisted(accessToken) && redisTemplate.hasKey("token:" + accessToken);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 리프레시 토큰을 검증하고 새로운 액세스 토큰을 발급합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @return 새로운 액세스 토큰, 실패 시 null
     */
    public String refreshAccessToken(UUID userId, String clientId, TokenPair jwtDto) {
        String userKey = "user:" + userId + ":" + clientId;
        String storedRefreshToken = (String) redisTemplate.opsForHash().get(userKey, "refreshToken");

        if (storedRefreshToken != null && storedRefreshToken.equals(jwtDto.refreshToken()) && validateRefreshToken(jwtDto.refreshToken())) {
            Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwtDto.refreshToken()).getPayload();
            String role = claims.get("role", String.class);
            blacklistToken(jwtDto.accessToken());
            return createAccessToken(userId, clientId, role);
        }

        return null;
    }

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     * @param refreshToken 리프레시 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    private boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 특정 기기에서 로그아웃합니다.
     * @param userId 사용자 ID
     * @param clientId 클라이언트 ID
     * @param accessToken 현재 액세스 토큰
     */
    public void logoutDevice(UUID userId, String clientId, String accessToken) {
        String userKey = "user:" + userId + ":" + clientId;
        String refreshToken = (String) redisTemplate.opsForHash().get(userKey, "refreshToken");

        redisTemplate.delete(userKey);
        redisTemplate.delete("token:" + accessToken);

        blacklistToken(accessToken);
        if (refreshToken != null) {
            blacklistToken(refreshToken);
        }
    }

    /**
     * 모든 기기에서 로그아웃합니다.
     * @param userId 사용자 ID
     */
    public void logoutAllDevices(UUID userId) {
        Set<String> keys = redisTemplate.keys("user:" + userId + ":*");
        if (keys != null) {
            for (String key : keys) {
                String refreshToken = (String) redisTemplate.opsForHash().get(key, "refreshToken");
                if (refreshToken != null) {
                    blacklistToken(refreshToken);
                }
            }
            redisTemplate.delete(keys);
        }

        Set<String> tokenKeys = redisTemplate.keys("token:*");
        if (tokenKeys != null) {
            for (String key : tokenKeys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value != null && value.startsWith(userId.toString())) {
                    String token = key.substring("token:".length());
                    blacklistToken(token);
                    redisTemplate.delete(key);
                }
            }
        }
    }

    /**
     * 토큰을 블랙리스트에 추가합니다.
     * @param token 블랙리스트에 추가할 토큰
     */
    private void blacklistToken(String token) {
        String blacklistKey = BLACK_LIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(blacklistKey, "true", accessTokenExpiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인합니다.
     * @param token 확인할 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(BLACK_LIST_KEY_PREFIX + token));
    }

    /**
     * 토큰에서 클레임을 추출합니다.
     * @param token JWT 토큰
     * @return 토큰의 클레임
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
