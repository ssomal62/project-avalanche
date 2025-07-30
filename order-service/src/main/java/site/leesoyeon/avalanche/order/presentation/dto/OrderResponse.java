package site.leesoyeon.avalanche.order.presentation.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

import java.util.UUID;

@Builder
public record OrderResponse(
        UUID userId,
        UUID orderId,
        int quantity,
        Integer finalAmount,
        String activityType,
        ProductInfo productInfo,
        ShippingInfo shippingInfo,
        OrderStatus status
) {
}