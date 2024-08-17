package site.leesoyeon.avalanche.product.presentation.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductDetailResponse> products
) {
}