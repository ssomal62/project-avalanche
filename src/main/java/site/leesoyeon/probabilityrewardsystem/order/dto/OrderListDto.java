package site.leesoyeon.probabilityrewardsystem.order.dto;

import org.springframework.data.domain.Page;

public record OrderListDto(
        Page<OrderDetailDto> orders
) {
}
