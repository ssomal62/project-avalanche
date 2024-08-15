package site.leesoyeon.probabilityrewardsystem.order.dto;

import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;

import java.util.UUID;

public record OrderSearchCondition(
        UUID userId,
        OrderStatus status
) {
}
