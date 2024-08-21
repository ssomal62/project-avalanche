package site.leesoyeon.avalanche.product.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.shared.enums.Rarity;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateRequest(

        @NotNull
        UUID productId,

        @Size(min = 1, message = "상품명은 비어있을 수 없습니다.")
        String name,

        String description,

        Rarity rarity,

        ProductStatus status,

        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @Size(min = 1, message = "카테고리명은 비어있을 수 없습니다.")
        String categoryName,

        @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
        Integer stock,

        String imageUrl,

        @DecimalMin(value = "0.0", message = "확률 배율은 0 이상이어야 합니다.")
        Double probabilityMultiplier
) {
}