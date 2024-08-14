package site.leesoyeon.probabilityrewardsystem.order.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class OrderException extends RuntimeException {

    private final ApiStatus status;

    public OrderException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}