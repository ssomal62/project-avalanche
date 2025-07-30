package site.leesoyeon.avalanche.auth.infrastructure.security.jwt;

import lombok.Builder;

@Builder
public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
