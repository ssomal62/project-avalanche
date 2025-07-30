package site.leesoyeon.avalanche.user.infrastructure.external.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.user.shared.enums.UserStatus;

@Builder
public record UpdatedAuthenticatedUserDto(
        boolean emailVerified,
        UserStatus status
) {
}
