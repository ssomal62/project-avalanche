package site.leesoyeon.avalanche.auth.infrastructure.external.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdatePasswordDto(
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        String newPassword
) {
}
