package site.leesoyeon.avalanche.user.infrastructure.external.dto;

import lombok.Builder;

@Builder
public record TokenValidationResponse(
        boolean isValid,
        String email
) {
}