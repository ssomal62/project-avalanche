package site.leesoyeon.avalanche.point.infrastructure.external.dto;

import java.util.UUID;

public record UserDto(
        UUID userId,
        String email,
        String nickname,
        String password,
        String role,
        String status
) {
}