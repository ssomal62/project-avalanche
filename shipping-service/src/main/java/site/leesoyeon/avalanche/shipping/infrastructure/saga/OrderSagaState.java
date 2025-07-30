package site.leesoyeon.avalanche.shipping.infrastructure.saga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.leesoyeon.avalanche.shipping.domain.model.ShippingInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSagaState {

    private UUID orderId;
    private UUID userId;
    private UUID shippingId;
    private ShippingInfo shippingInfo;
    private String status;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant lastUpdated = Instant.now();

    @Builder.Default
    private Map<String, String> commandStatuses = new HashMap<>();

    @Builder.Default
    private Map<String, String> compensationStatuses = new HashMap<>();

}
