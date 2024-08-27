package site.leesoyeon.avalanche.order.infrastructure.saga.enums;

import lombok.Getter;

@Getter
public enum CommandType {
        CHECK_STOCK, APPLY_POINTS, PREPARE_SHIPPING, TIMEOUT
}