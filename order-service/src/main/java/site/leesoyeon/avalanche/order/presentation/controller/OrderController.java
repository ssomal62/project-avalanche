package site.leesoyeon.avalanche.order.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.order.presentation.dto.OrderDetailDto;
import site.leesoyeon.avalanche.order.presentation.dto.OrderListDto;
import site.leesoyeon.avalanche.order.infrastructure.external.dto.OrderRequestDto;
import site.leesoyeon.avalanche.order.presentation.dto.OrderSearchCondition;
import site.leesoyeon.avalanche.order.saga.coordinator.OrderSagaCoordinator;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;
import site.leesoyeon.avalanche.order.application.service.OrderService;


import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderSagaCoordinator orderSagaCoordinator;
    private final OrderService orderService;

    /**
     * 주문을 생성하는 메서드입니다.
     *
     * @param orderRequest 주문 요청 데이터를 포함하는 객체입니다. 사용자 ID, 주문 항목, 배송 정보를 포함합니다.
     * @return 주문 컨텍스트를 포함하는 ResponseEntity 객체입니다. 주문 생성에 실패한 경우 500 상태 코드를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<OrderContext> createOrder(@RequestBody OrderRequestDto orderRequest) {
        OrderContext initialContext = OrderContext.builder()
                .userId(orderRequest.userId())
                .quantity(orderRequest.quantity())
                .transactionInfo(orderRequest.transactionInfo())
                .productInfo(orderRequest.productInfo())
                .shippingInfo(orderRequest.shippingInfo())
                .build();

        OrderContext resultContext = orderSagaCoordinator.execute(initialContext);
        if (resultContext.isFailed()) {
            return ResponseEntity.status(500).body(resultContext);
        } else {
            return ResponseEntity.ok(resultContext);
        }
    }

    /**
     * 특정 주문의 상세 정보를 조회하는 메서드입니다.
     *
     * @param orderId 조회할 주문의 UUID입니다.
     * @return 주문 상세 정보를 포함하는 ResponseEntity 객체입니다.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable(value = "orderId") UUID orderId) {
        OrderDetailDto orderDetail = orderService.getOrderDetail(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDetail);
    }

    /**
     * 검색 조건과 페이징 정보를 기반으로 주문 목록을 조회하는 메서드입니다.
     *
     * @param condition 검색 조건을 포함하는 객체입니다.
     * @param pageable  페이징 정보를 포함하는 객체입니다.
     * @return 검색 조건에 맞는 주문 목록을 포함하는 ResponseEntity 객체입니다.
     */
    @PostMapping("/history")
    public ResponseEntity<OrderListDto> getOrderDetail(@Valid @RequestBody OrderSearchCondition condition, Pageable pageable) {
        OrderListDto orders = orderService.getOrderList(condition, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }
}