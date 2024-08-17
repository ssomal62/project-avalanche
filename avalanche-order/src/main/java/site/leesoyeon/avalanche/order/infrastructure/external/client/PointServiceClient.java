package site.leesoyeon.avalanche.order.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;

@FeignClient(name = "avalanche-point", url = "${feign.client.config.avalanche-point.url}")
public interface PointServiceClient {

    @PostMapping("/api/v1/points/deduct")
    OrderContext deductPoints(@RequestBody OrderContext context);

    @PostMapping("/api/v1/points/refund")
    OrderContext refundPoints(@RequestBody OrderContext context);
}