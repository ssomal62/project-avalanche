package site.leesoyeon.avalanche.point.domain.model;



import site.leesoyeon.avalanche.point.shared.enums.ActivityType;

import java.util.UUID;

@lombok.Builder(toBuilder = true)
public record PointTransactionInfo(
        UUID transactionId,
        Integer amount,
        ActivityType activityType,
        String description
) {
}
