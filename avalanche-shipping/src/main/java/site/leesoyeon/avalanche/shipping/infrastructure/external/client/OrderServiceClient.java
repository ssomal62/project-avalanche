package site.leesoyeon.avalanche.shipping.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.shipping.infrastructure.external.dto.OrderDto;
import site.leesoyeon.avalanche.shipping.domain.model.ShippingInfo;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @PutMapping("/api/v1/orders")
    void updateOrder(@RequestBody OrderDto order);

    @GetMapping("/api/v1/orders/shipping/{shippingId}")
    Optional<OrderDto> findByShippingId(@PathVariable("shippingId") UUID shippingId);

    @GetMapping("/api/v1/orders/{orderId}")
    Optional<OrderDto> findById(@PathVariable("orderId") UUID orderId);

    @PutMapping("/api/v1/orders/{orderId}")
    void updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderDto orderDto);

    @PatchMapping("/api/v1/orders/{orderId}/shipping")
    void updateShippingInfo(@PathVariable("orderId") UUID orderId, @RequestBody ShippingInfo shippingInfo);

    @DeleteMapping("/api/v1/orders/{orderId}/shipping")
    void removeShippingInfo(@PathVariable("orderId") UUID orderId);
}
