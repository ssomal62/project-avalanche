package site.leesoyeon.probabilityrewardsystem.shipping.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ShippingInfo(
        UUID shippingId,
        String recipientName,
        String recipientPhone,
        String address,
        String detailedAddress,
        String zipCode
) {
}
