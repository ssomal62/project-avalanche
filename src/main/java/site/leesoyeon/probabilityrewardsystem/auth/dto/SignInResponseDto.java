package site.leesoyeon.probabilityrewardsystem.auth.dto;

import lombok.Builder;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserRole;

import java.util.UUID;

@Builder
public record SignInResponseDto(
        UUID userId,
        String nickname,
        UserRole role,
        String grantType,
        String accessToken,
        String refreshToken,
        Long expiresIn

) {
}
