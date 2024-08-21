package site.leesoyeon.avalanche.user.presentation.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateUserInfoRequestDto(
        String nickname,

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다.")
        String phone,

        String address,

        String detailedAddress
) {
}
