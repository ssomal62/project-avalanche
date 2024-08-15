package site.leesoyeon.probabilityrewardsystem.saga.event;

public interface EventPublisher {

    /**
     * 이벤트를 발행합니다.
     *
     * @param event 이벤트 객체, 발행할 이벤트의 구체적인 내용
     */
    void publish(SagaEvent event);
}