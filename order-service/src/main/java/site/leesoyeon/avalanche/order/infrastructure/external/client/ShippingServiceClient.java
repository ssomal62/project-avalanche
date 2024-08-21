package site.leesoyeon.avalanche.order.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;

@FeignClient(name = "shipping-service", url = "${feign.client.config.shipping-service.url")
public interface ShippingServiceClient {

    @PostMapping("/api/v1/shipping/create")
    OrderContext createShipping(@RequestBody OrderContext context);

    @PostMapping("/api/v1/shipping/cancel")
    OrderContext cancelShipping(@RequestBody OrderContext context);
}