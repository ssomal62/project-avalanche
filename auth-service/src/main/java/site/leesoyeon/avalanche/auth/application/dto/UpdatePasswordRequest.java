package site.leesoyeon.avalanche.auth.application.dto;

import java.util.UUID;

public record UpdatePasswordRequest(
        UUID userId,
        String newPassword
) {
}
