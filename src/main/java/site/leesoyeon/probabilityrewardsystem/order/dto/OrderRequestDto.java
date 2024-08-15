package site.leesoyeon.probabilityrewardsystem.order.dto;

import site.leesoyeon.probabilityrewardsystem.point.domain.PointTransactionInfo;
import site.leesoyeon.probabilityrewardsystem.product.domain.ProductInfo;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingInfo;

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
