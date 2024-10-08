package site.leesoyeon.avalanche.order.presentation.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

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
