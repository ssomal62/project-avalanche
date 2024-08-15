package site.leesoyeon.probabilityrewardsystem.order.exception;

/**
 * 주문 상태와 관련된 예외를 처리하기 위한 클래스
 */
public class OrderStatusException extends RuntimeException {

    /**
     * 지정된 오류 메시지로 새로운 OrderStatusException을 생성합니다.
     *
     * @param message 오류 메시지
     */
    public OrderStatusException(String message) {
        super(message);
    }

    /**
     * 지정된 오류 메시지와 원인으로 새로운 OrderStatusException을 생성합니다.
     *
     * @param message 오류 메시지
     * @param cause 예외의 원인
     */
    public OrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
