package site.leesoyeon.avalanche.order.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import site.leesoyeon.avalanche.order.application.service.OrderCreationService;
import site.leesoyeon.avalanche.order.application.service.OrderService;
import site.leesoyeon.avalanche.order.presentation.dto.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderCreationService orderCreationService;

    @PostMapping
    public DeferredResult<ResponseEntity<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        DeferredResult<ResponseEntity<OrderResponse>> deferredResult = new DeferredResult<>(300000L); // 5분 타임아웃

        CompletableFuture<OrderResponse> future = orderCreationService.createAndProcessOrder(request);

        future.thenAccept(response -> deferredResult.setResult(ResponseEntity.ok(response)))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof IllegalStateException) {
                        deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                    } else {
                        deferredResult.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
                    }
                    return null;
                });

        deferredResult.onTimeout(() ->
                deferredResult.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(null))
        );

        return deferredResult;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable(value = "orderId") UUID orderId) {
        OrderDetailDto orderDetail = orderService.getOrderDetail(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDetail);
    }

    @PostMapping("/history")
    public ResponseEntity<OrderListDto> getOrderDetail(@Valid @RequestBody OrderSearchCondition condition, Pageable pageable) {
        OrderListDto orders = orderService.getOrderList(condition, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }
}