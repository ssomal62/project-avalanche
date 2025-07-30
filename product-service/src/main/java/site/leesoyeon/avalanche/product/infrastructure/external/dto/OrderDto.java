package site.leesoyeon.avalanche.product.infrastructure.external.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record OrderDto(
        UUID orderId,
        String status
) {

}
