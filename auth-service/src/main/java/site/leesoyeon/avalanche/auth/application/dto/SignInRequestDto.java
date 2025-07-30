package site.leesoyeon.avalanche.auth.application.dto;

import jakarta.validation.constraints.*;

public record SignInRequestDto(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        String password
) {
}
