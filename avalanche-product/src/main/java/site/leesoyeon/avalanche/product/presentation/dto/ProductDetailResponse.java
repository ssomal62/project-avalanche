package site.leesoyeon.avalanche.product.presentation.dto;


import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.shared.enums.Rarity;

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
