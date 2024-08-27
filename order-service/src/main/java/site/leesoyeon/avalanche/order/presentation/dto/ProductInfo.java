package site.leesoyeon.avalanche.order.presentation.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ProductInfo(
        UUID productId,
        String productName,
        int unitPrice
) {
}