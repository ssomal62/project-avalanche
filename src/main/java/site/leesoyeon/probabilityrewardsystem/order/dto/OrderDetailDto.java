package site.leesoyeon.probabilityrewardsystem.order.dto;

import lombok.Builder;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;

import java.util.UUID;

@Builder
public record OrderDetailDto(
        UUID orderId,
        UUID userId,
        UUID productId,
        Integer quantity,
        OrderStatus status,
        Integer usedPoints,
        UUID shippingId
) {

}
