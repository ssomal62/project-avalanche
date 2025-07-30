package site.leesoyeon.avalanche.shipping.presentation.dto;

import site.leesoyeon.avalanche.shipping.shared.enums.ShippingStatus;

import java.util.UUID;

public record ShippingStatusDto(
        UUID shippingId,
        ShippingStatus status
) {
}