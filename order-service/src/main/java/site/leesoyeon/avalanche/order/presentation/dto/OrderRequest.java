package site.leesoyeon.avalanche.order.presentation.dto;

import java.util.UUID;

public record OrderRequest(
        UUID userId,
        int quantity,
        Integer amount,
        String activityType,
        ProductInfo productInfo,
        ShippingInfo shippingInfo
) {
}