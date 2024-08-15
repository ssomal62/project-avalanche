package site.leesoyeon.probabilityrewardsystem.shipping.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class ShippingException extends RuntimeException {

    private final ApiStatus status;

    public ShippingException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
