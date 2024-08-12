package site.leesoyeon.probabilityrewardsystem.jwt.dto;


public record JwtRequestDto(
        String clientId,
        String accessToken,
        String refreshToken
) {
}