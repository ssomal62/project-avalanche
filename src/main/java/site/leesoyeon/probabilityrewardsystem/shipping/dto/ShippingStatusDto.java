package site.leesoyeon.probabilityrewardsystem.shipping.dto;

import site.leesoyeon.probabilityrewardsystem.shipping.enums.ShippingStatus;

import java.util.UUID;

public record ShippingStatusDto(
        UUID shippingId,
        ShippingStatus status
) {
}