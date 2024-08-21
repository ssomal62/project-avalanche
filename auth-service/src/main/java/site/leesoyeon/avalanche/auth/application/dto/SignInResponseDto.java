package site.leesoyeon.avalanche.auth.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SignInResponseDto(
        UUID userId,
        String nickname,
        String role,
        String grantType,
        String accessToken,
        String refreshToken,
        Long expiresIn

) {
}
