package site.leesoyeon.probabilityrewardsystem.jwt.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String grantType,
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn
) {

}
