package site.leesoyeon.avalanche.point.infrastructure.external.dto;

import java.util.UUID;

public record ProductInfo(
        UUID productId,
        String name
) {
}