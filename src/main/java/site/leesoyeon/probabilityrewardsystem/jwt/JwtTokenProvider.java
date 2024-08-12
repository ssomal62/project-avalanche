package site.leesoyeon.probabilityrewardsystem.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtResponseDto;
import site.leesoyeon.probabilityrewardsystem.user.security.AuthenticationUserDetailsService;

import java.util.concurrent.TimeUnit;

import static site.leesoyeon.probabilityrewardsystem.common.Constants.AUTHORIZATION_HEADER;
import static site.leesoyeon.probabilityrewardsystem.common.Constants.BEARER_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    private final JwtKeyProvider jwtKeyProvider;
    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenValidator tokenValidator;
    private final RefreshTokenService refreshTokenService;

    private final AuthenticationUserDetailsService userDetailsService;

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return Jwts.parser().setSigningKey(jwtKeyProvider.getKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                return false;
            }
            Jwts.parser().setSigningKey(jwtKeyProvider.getKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public JwtResponseDto createAndSaveJwtToken(String email, String clientId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        JwtResponseDto jwtResponseDto = tokenGenerator.createJwtToken(authentication);

        refreshTokenService.saveRefreshToken(email, clientId, jwtResponseDto.refreshToken());
        return jwtResponseDto;
    }

    public String createEmailVerificationToken(String email) {
        return tokenGenerator.createEmailVerificationToken(email);
    }

    public void checkIfTokenInvalidated(String token) {
        refreshTokenService.checkIfTokenInvalidated(token);
    }

    public Claims getInfoFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtKeyProvider.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public void invalidateToken(String token) {
        Claims claims = getInfoFromToken(token);
        long expirationTime = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        long ttl = expirationTime - now;

        if (ttl > 0) {
            redisTemplate.opsForValue().set(
                    "blacklist:" + token,
                    "true",
                    ttl,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void logoutAllClients(String email, String currentToken) {
        refreshTokenService.deleteAllRefreshTokens(email);
        invalidateToken(currentToken);
        SecurityContextHolder.clearContext();
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}