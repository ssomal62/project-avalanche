package site.leesoyeon.avalanche.point.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.event.PointAppliedEvent;
import site.leesoyeon.avalanche.avro.event.PointRefundedEvent;


@Component
@RequiredArgsConstructor
public class PointProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.point-applied}")
    private String POINT_APPLIED_TOPIC;

    @Value("${app.kafka.topics.point-refunded}")
    private String POINT_REFUNDED_TOPIC;


    public void sendPointAppliedEvent(PointAppliedEvent event) {
        kafkaTemplate.send(POINT_APPLIED_TOPIC, event.getOrderId(), event);
    }

    public void sendPointRefundedEvent(PointRefundedEvent event) {
        kafkaTemplate.send(POINT_REFUNDED_TOPIC, event.getOrderId(), event);
    }

}