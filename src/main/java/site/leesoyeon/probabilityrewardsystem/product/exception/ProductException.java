package site.leesoyeon.probabilityrewardsystem.product.exception;

import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

@Getter
public class ProductException extends RuntimeException {

    private final ApiStatus status;

    public ProductException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}