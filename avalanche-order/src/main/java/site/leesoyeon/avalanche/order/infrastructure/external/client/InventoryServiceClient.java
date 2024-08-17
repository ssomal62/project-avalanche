package site.leesoyeon.avalanche.order.infrastructure.external.client;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;

@FeignClient(name = "avalanche-product", url = "${feign.client.config.avalanche-product.url}")
public interface InventoryServiceClient {

    @PostMapping("/api/v1/product/deduct")
    OrderContext deductInventory(@RequestBody OrderContext context);

    @PostMapping("/api/v1/product/refund")
    OrderContext refundInventory(@RequestBody OrderContext context);
}