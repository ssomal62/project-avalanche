package site.leesoyeon.avalanche.order.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;

@FeignClient(name = "avalanche-shipping", url = "${feign.client.config.avalanche-shipping.url}")
public interface ShippingServiceClient {

    @PostMapping("/api/v1/shipping/create")
    OrderContext createShipping(@RequestBody OrderContext context);

    @PostMapping("/api/v1/shipping/cancel")
    OrderContext cancelShipping(@RequestBody OrderContext context);
}