package site.leesoyeon.avalanche.auth.presentation.dto;


public record JwtRequestDto(
        String accessToken,
        String refreshToken
) {
}