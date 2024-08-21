package site.leesoyeon.avalanche.order.infrastructure.external.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record PointTransactionInfo(
        UUID transactionId,
        Integer amount,
        String activityType,
        String description
) {
}
