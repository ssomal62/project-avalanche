package site.leesoyeon.avalanche.order.presentation.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ShippingInfo(
        String recipientName,
        String recipientPhone,
        String address,
        String detailedAddress,
        String zipCode
) {
}
