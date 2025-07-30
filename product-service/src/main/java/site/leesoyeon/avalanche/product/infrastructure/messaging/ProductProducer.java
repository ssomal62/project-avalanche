package site.leesoyeon.avalanche.product.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.avro.event.StockCheckedEvent;
import site.leesoyeon.avalanche.avro.event.StockReleasedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.stock-checked}")
    private String STOCK_CHECKED_TOPIC;

    @Value("${app.kafka.topics.stock-released}")
    private String STOCK_RELEASED_TOPIC;

    public void sendStockCheckedEvent(StockCheckedEvent event) {
        kafkaTemplate.send(STOCK_CHECKED_TOPIC, event.getOrderId(), event);
    }

    public void sendStockReleased(StockReleasedEvent event) {
        kafkaTemplate.send(STOCK_RELEASED_TOPIC, event.getOrderId(), event);
    }
}