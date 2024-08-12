package site.leesoyeon.probabilityrewardsystem.jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.auth.exception.AuthException;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.*;

@Component
public class JwtTokenValidator {

    private final JwtKeyProvider jwtKeyProvider;

    public JwtTokenValidator(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtKeyProvider.getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new AuthException(INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthException(INVALID_JWT_TOKEN);
        }
    }
}