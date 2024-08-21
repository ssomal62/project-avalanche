package site.leesoyeon.avalanche.auth.infrastructure.external.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record UserDto(
        UUID userId,
        String email,
        String nickname,
        String password,
        String role,
        String status
) {
}