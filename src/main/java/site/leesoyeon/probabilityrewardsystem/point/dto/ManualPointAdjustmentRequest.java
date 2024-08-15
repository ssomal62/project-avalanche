package site.leesoyeon.probabilityrewardsystem.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ManualPointAdjustmentRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "포인트 금액은 필수입니다.")
        @Min(value = 1, message = "포인트 금액은 1 이상이어야 합니다.")
        Integer amount,

        @NotNull(message = "활동 유형은 필수입니다.")
        ActivityType activityType,

        UUID productId,

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
        String description,

        LocalDateTime expiryDate
) {}
