package site.leesoyeon.avalanche.order.presentation.dto;

import org.springframework.data.domain.Page;

public record OrderListDto(
        Page<OrderDetailDto> orders
) {
}
