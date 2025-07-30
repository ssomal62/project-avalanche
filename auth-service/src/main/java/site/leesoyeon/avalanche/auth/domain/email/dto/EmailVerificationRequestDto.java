package site.leesoyeon.avalanche.auth.domain.email.dto;

import lombok.Builder;

@Builder
public record EmailVerificationRequestDto(
        String token
) {
}
