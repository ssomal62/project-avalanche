package site.leesoyeon.probabilityrewardsystem.point.domain;

import lombok.Builder;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

import java.util.UUID;

@Builder(toBuilder = true)
public record PointTransactionInfo(
        UUID transactionId,
        Integer amount,
        ActivityType activityType,
        String description
) {
}
