package site.leesoyeon.avalanche.shipping.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.event.ShippingCancelledEvent;
import site.leesoyeon.avalanche.avro.event.ShippingPreparedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.shipping-prepared}")
    private String SHIPPING_PREPARED_TOPIC;

    @Value("${app.kafka.topics.shipping-cancelled}")
    private String SHIPPING_CANCELLED_TOPIC;

    public void sendShippingPreparedEvent(ShippingPreparedEvent event) {
        kafkaTemplate.send(SHIPPING_PREPARED_TOPIC, event.getOrderId(), event);
    }

    public void sendShippingCancelledEvent(ShippingCancelledEvent event) {
        kafkaTemplate.send(SHIPPING_CANCELLED_TOPIC, event.getOrderId(), event);
    }

}