package site.leesoyeon.avalanche.product.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.product.infrastructure.external.dto.OrderDto;
import site.leesoyeon.avalanche.product.domain.model.ProductInfo;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @PutMapping("/api/v1/orders")
    void updateOrder(@RequestBody OrderDto order);

    @GetMapping("/api/v1/orders/shipping/{productId}")
    Optional<OrderDto> findByProductId(@PathVariable("productId") UUID productId);

    @GetMapping("/api/v1/orders/{orderId}")
    Optional<OrderDto> findById(@PathVariable("orderId") UUID orderId);

    @PutMapping("/api/v1/orders/{orderId}")
    void updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderDto orderDto);

    @PatchMapping("/api/v1/orders/{orderId}/product")
    void updateProductInfo(@PathVariable("orderId") UUID orderId, @RequestBody ProductInfo productInfo);
}
