package site.leesoyeon.avalanche.order.infrastructure.saga.event;

import java.util.UUID;

public record OrderCancelledEvent(UUID orderId) {
}