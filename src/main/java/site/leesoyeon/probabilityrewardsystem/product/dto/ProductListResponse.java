package site.leesoyeon.probabilityrewardsystem.product.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductDetailResponse> products
) {
}