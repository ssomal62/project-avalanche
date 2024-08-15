package site.leesoyeon.probabilityrewardsystem.order.dto;

import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingInfo;

import java.util.UUID;

public record OrderRequestDto(

        UUID userId,
        UUID orderId,
        OrderItem orderItem,
        ShippingInfo shippingInfo
) {
}
