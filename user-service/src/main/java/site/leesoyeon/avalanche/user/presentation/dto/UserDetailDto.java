package site.leesoyeon.avalanche.user.presentation.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.user.shared.enums.UserRole;

@Builder
public record UserDetailDto (
        String nickname,
        String address,
        UserRole role
) {
}
