package site.leesoyeon.avalanche.order.infrastructure.external.dto;

import java.util.UUID;

public record ProductInfo(
        UUID productId,
        String name,
        Integer unitPrice
) {
}