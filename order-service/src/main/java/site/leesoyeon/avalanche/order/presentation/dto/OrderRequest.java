package site.leesoyeon.avalanche.order.presentation.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderRequest(
        UUID userId,
        int quantity,
        Integer amount,
        String activityType,
        ProductInfo productInfo,
        ShippingInfo shippingInfo
) {
}