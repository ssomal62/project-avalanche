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
import java.util.concurrent.TimeUnit;

import static site.leesoyeon.avalanche.auth.application.util.Constants.BLACK_LIST_KEY_PREFIX;

@Component
@RequiredArgsConstructor
public class EmailJwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.email-secret}")
    private String emailSecretKey;

    @Value("${jwt.email-token-expiration}")
    private long emailTokenExpiration;

    /**
     * 이메일 토큰 서명에 사용할 키를 생성합니다.
     *
     * @return SecretKey 객체
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(emailSecretKey.getBytes());
    }

    /**
     * 이메일 인증 토큰을 생성합니다.
     *
     * @param email 사용자 이메일
     * @return 생성된 이메일 인증 토큰
     */
    public String createEmailVerificationToken(String email) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(emailTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 이메일 토큰의 유효성을 검증합니다.
     *
     * @param token 이메일 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return !isTokenBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 클레임을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰의 클레임
     */
    public Claims getInfoFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰을 블랙리스트에 추가합니다.
     *
     * @param token 블랙리스트에 추가할 토큰
     */
    private void blacklistToken(String token) {
        String blacklistKey = BLACK_LIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(blacklistKey, "true", emailTokenExpiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인합니다.
     *
     * @param token 확인할 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(BLACK_LIST_KEY_PREFIX + token));
    }
}
