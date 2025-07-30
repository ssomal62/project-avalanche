package site.leesoyeon.avalanche.product.infrastructure.exception;

import lombok.Getter;
import site.leesoyeon.avalanche.product.shared.api.ApiStatus;

@Getter
public class ProductException extends RuntimeException {

    private final ApiStatus status;
    private final String debugMessage;

    public ProductException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = null;
    }

    public ProductException(ApiStatus status, String debugMessage) {
        super(status.getMessage());
        this.status = status;
        this.debugMessage = debugMessage;
    }
}