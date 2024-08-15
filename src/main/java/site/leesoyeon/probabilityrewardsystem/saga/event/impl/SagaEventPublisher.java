package site.leesoyeon.probabilityrewardsystem.saga.event.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.event.SagaEvent;

@Component
@RequiredArgsConstructor
public class SagaEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(SagaEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}