package site.leesoyeon.probabilityrewardsystem.product.dto;

import site.leesoyeon.probabilityrewardsystem.product.enums.ProductStatus;
import site.leesoyeon.probabilityrewardsystem.product.enums.Rarity;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailResponse(
        UUID productId,
        String name,
        String description,
        Rarity rarity,
        ProductStatus status,
        BigDecimal price,
        String categoryName,
        Integer stock,
        String imageUrl,
        Double probabilityMultiplier,
        double effectiveDropRate
) {
}
