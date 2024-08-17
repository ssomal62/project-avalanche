package site.leesoyeon.avalanche.point.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import site.leesoyeon.avalanche.point.shared.enums.ActivityType;


import java.time.LocalDateTime;
import java.util.UUID;

public record PointTransactionSummaryDto(
        @NotNull ActivityType activityType,
        @NotNull Integer amount,
        @NotNull @PositiveOrZero Integer balance,
        UUID productId,
        @Size(max = 500) String description,
        LocalDateTime expiryDate,
        @NotNull LocalDateTime createdDate
) {
}
