package site.leesoyeon.probabilityrewardsystem.product.domain;

import java.util.UUID;

public record ProductInfo(
        UUID productId,
        String name,
        Integer unitPrice
) {
}