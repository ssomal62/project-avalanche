package site.leesoyeon.probabilityrewardsystem.email.dto;

import lombok.Builder;

@Builder
public record EmailVerificationRequestDto(
        String token
) {
}
