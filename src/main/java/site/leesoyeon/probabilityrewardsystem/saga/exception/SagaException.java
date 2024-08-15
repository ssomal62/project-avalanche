package site.leesoyeon.probabilityrewardsystem.saga.exception;


/**
 * 사가 패턴에서 발생하는 예외를 처리하기 위한 사용자 정의 예외 클래스입니다.
 * 주로 보상 작업이 실패하거나, 사가 실행 중에 예기치 않은 오류가 발생할 때 사용됩니다.
 */
public class SagaException extends RuntimeException {

    /**
     * 기본 생성자.
     */
    public SagaException() {
        super();
    }

    /**
     * 예외 메시지와 원인(또는 발생 원인 예외)을 받아 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 발생 원인 예외
     */
    public SagaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 예외 메시지를 받아 생성합니다.
     *
     * @param message 예외 메시지
     */
    public SagaException(String message) {
        super(message);
    }

    /**
     * 발생 원인 예외를 받아 생성합니다.
     *
     * @param cause 발생 원인 예외
     */
    public SagaException(Throwable cause) {
        super(cause);
    }
}