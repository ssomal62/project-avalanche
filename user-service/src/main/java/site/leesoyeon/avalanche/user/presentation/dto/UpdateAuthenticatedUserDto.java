package site.leesoyeon.avalanche.user.presentation.dto;

import site.leesoyeon.avalanche.user.shared.enums.UserStatus;

public record UpdateAuthenticatedUserDto(
        String email,
        boolean emailVerified,
        UserStatus status
) {
}
