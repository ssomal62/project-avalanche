package site.leesoyeon.avalanche.user.infrastructure.external.dto;

import lombok.Builder;

@Builder
public record TokenValidationRequest(
        String token
) {
}
