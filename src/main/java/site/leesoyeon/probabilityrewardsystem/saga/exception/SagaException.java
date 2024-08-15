package site.leesoyeon.probabilityrewardsystem.saga.exception;


import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

/**
 * 사가 패턴에서 발생하는 예외를 처리하기 위한 사용자 정의 예외 클래스입니다.
 * 주로 보상 작업이 실패하거나, 사가 실행 중에 예기치 않은 오류가 발생할 때 사용됩니다.
 */
public class SagaException extends RuntimeException {
    private final OrderContext context;

    public SagaException(String message, OrderContext context) {
        super(message);
        this.context = context;
    }

    public OrderContext getContext() {
        return context;
    }
}