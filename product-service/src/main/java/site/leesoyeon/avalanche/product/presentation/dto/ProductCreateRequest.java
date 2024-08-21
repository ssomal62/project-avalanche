package site.leesoyeon.avalanche.product.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.shared.enums.Rarity;


import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수 입력 항목입니다.")
        String name,

        String description,

        @NotNull(message = "희귀도는 필수 선택 항목입니다.")
        Rarity rarity,

        @NotNull(message = "상품 상태는 필수 선택 항목입니다.")
        ProductStatus status,

        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @NotBlank(message = "카테고리명은 필수 입력 항목입니다.")
        String categoryName,

        @NotNull(message = "수량은 필수 입력 항목입니다.")
        @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
        Integer stock,

        String imageUrl,

        @DecimalMin(value = "0.0", message = "확률 배율은 0 이상이어야 합니다.")
        Double probabilityMultiplier
) {
}
