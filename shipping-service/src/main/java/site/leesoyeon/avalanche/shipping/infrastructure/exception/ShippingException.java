package site.leesoyeon.avalanche.shipping.infrastructure.exception;


import lombok.Getter;
import site.leesoyeon.avalanche.shipping.shared.api.ApiStatus;

@Getter
public class ShippingException extends RuntimeException {

    private final ApiStatus status;
    private final String debugMessage;

    public ShippingException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = null;
    }

    public ShippingException(ApiStatus status, String debugMessage) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = debugMessage;
    }
}