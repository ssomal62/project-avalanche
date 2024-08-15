package site.leesoyeon.probabilityrewardsystem.order.dto;

import java.util.UUID;

public record OrderItem(

        UUID productId,
        String name,
        Integer quantity,
        Integer unitPrice
) {
}
