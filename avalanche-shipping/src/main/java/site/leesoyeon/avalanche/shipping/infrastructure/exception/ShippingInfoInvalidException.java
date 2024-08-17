package site.leesoyeon.avalanche.shipping.infrastructure.exception;

/**
 * 배송 정보가 유효하지 않을 때 발생하는 예외
 */
public class ShippingInfoInvalidException extends RuntimeException {

    /**
     * 지정된 오류 메시지로 새로운 ShippingInfoInvalidException을 생성합니다.
     *
     * @param message 오류 메시지
     */
    public ShippingInfoInvalidException(String message) {
        super(message);
    }

    /**
     * 지정된 오류 메시지와 원인으로 새로운 ShippingInfoInvalidException을 생성합니다.
     *
     * @param message 오류 메시지
     * @param cause 예외의 원인
     */
    public ShippingInfoInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}