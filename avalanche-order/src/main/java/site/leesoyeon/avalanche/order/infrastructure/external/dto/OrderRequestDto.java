package site.leesoyeon.avalanche.order.infrastructure.external.dto;

import java.util.UUID;

public record OrderRequestDto(
        UUID userId,
        UUID orderId,
        Integer quantity,
        PointTransactionInfo transactionInfo,
        ProductInfo productInfo,
        ShippingInfo shippingInfo
) {
}
