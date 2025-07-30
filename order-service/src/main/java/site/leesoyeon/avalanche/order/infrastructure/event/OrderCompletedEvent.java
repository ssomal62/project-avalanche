package site.leesoyeon.avalanche.order.infrastructure.event;

import java.util.UUID;

public record OrderCompletedEvent(UUID orderId) {
}