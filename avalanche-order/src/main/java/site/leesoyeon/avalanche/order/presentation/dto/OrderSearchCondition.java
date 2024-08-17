package site.leesoyeon.avalanche.order.presentation.dto;


import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

import java.util.UUID;

public record OrderSearchCondition(
        UUID userId,
        OrderStatus status
) {
}
