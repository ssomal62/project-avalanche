package site.leesoyeon.avalanche.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

import static site.leesoyeon.avalanche.api.util.Constants.BLACK_LIST_KEY_PREFIX;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * JWT 서명에 사용할 키를 생성합니다.
     * @return SecretKey 객체
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
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
     * 리프레시 토큰의 유효성을 검증합니다.
     * @param refreshToken 리프레시 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getClientIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("clientId", String.class);
    }
}
