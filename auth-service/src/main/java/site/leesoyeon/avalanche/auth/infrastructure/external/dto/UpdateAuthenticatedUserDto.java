package site.leesoyeon.avalanche.auth.infrastructure.external.dto;

import lombok.Builder;

@Builder
public record UpdateAuthenticatedUserDto(
        String email,
        boolean emailVerified,
        String status
) {
}
