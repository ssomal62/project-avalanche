package site.leesoyeon.avalanche.order.saga.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.order.infrastructure.external.dto.PointTransactionInfo;
import site.leesoyeon.avalanche.order.infrastructure.external.dto.ProductInfo;
import site.leesoyeon.avalanche.order.infrastructure.external.dto.ShippingInfo;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;
import site.leesoyeon.avalanche.order.saga.state.SagaState;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Builder(toBuilder = true)
public record OrderContext(
        UUID userId,
        UUID orderId,
        OrderStatus orderStatus,
        Integer quantity,
        PointTransactionInfo transactionInfo,
        ProductInfo productInfo,
        ShippingInfo shippingInfo,
        LocalDateTime createdDate,
        SagaState state,

        boolean pointStepCompleted,
        boolean inventoryStepCompleted,
        boolean orderStepCompleted,
        boolean shippingStepCompleted,

        boolean success,
        String errorMessage
) {

    // 사가 상태를 설정하는 메서드
    public OrderContext setState(SagaState state) {
        return this.toBuilder()
                .state(state)
                .build();
    }

    // 단계가 실패했는지 확인하는 메서드
    public boolean isFailed() {
        return !this.success;
    }

    //===================================
    //          포인트 관리 메서드
    //===================================

    // 포인트 차감 성공 시 호출
    public OrderContext pointDeducted(PointTransactionInfo transactionInfo) {
        PointTransactionInfo updatedTransactionInfo = this.transactionInfo().toBuilder()
                .transactionId(transactionInfo.transactionId())
                .description(transactionInfo.description())
                .build();

        return this.toBuilder()
                .transactionInfo(updatedTransactionInfo)
                .state(SagaState.POINT_DEDUCTED)
                .pointStepCompleted(true)
                .success(true)
                .build();
    }

    // 포인트 차감 실패 시 호출
    public OrderContext pointDeductionFailed(String errorMessage) {
        return this.toBuilder()
                .state(SagaState.POINT_DEDUCTION_FAILED)
                .pointStepCompleted(false)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 포인트 환불 시 호출
    public OrderContext pointRefunded() {
        return this.toBuilder()
                .transactionInfo(null)
                .state(SagaState.POINT_DEDUCTION_FAILED)
                .pointStepCompleted(false)
                .build();
    }

    //===================================
    //           재고 관리 메서드
    //===================================

    // 재고 차감 성공 시 호출
    public OrderContext inventoryDeducted() {
        return this.toBuilder()
                .state(SagaState.INVENTORY_DEDUCTED)
                .inventoryStepCompleted(true)
                .success(true)
                .build();
    }

    // 재고 차감 실패 시 호출
    public OrderContext inventoryDeductionFailed(String errorMessage) {
        return this.toBuilder()
                .state(SagaState.INVENTORY_DEDUCTION_FAILED)
                .inventoryStepCompleted(false)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 재고 복구 시 호출
    public OrderContext inventoryRefunded() {
        return this.toBuilder()
                .state(SagaState.INVENTORY_DEDUCTION_FAILED)
                .inventoryStepCompleted(false)
                .build();
    }

    //===================================
    //           주문 관리 메서드
    //===================================

    // 주문 생성 시 호출
    public OrderContext orderCreated(UUID orderId) {
        return this.toBuilder()
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATED)
                .state(SagaState.ORDER_CREATED)
                .orderStepCompleted(true)
                .success(true)
                .build();
    }

    // 주문 생성 실패 시 호출
    public OrderContext orderCreationFailed(String errorMessage) {
        return this.toBuilder()
                .state(SagaState.ORDER_CREATION_FAILED)
                .orderStepCompleted(false)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 주문 완료 시 호출
    public OrderContext completeOrder() {
        return this.toBuilder()
                .orderStatus(OrderStatus.COMPLETED)
                .state(SagaState.ORDER_FINALIZED)
                .success(true)
                .build();
    }

    // 주문 취소 시 호출
    public OrderContext orderCancelled() {
        return this.toBuilder()
                .orderId(null)
                .orderStatus(OrderStatus.CANCELLED)
                .state(SagaState.ORDER_CREATION_FAILED)
                .orderStepCompleted(false)
                .success(false)
                .build();
    }

    //===================================
    //           배송 관리 메서드
    //===================================

    // 배송 생성 시 호출
    public OrderContext shippingCreated(UUID shippingId) {
        ShippingInfo updatedShippingInfo = this.shippingInfo().toBuilder()
                .shippingId(shippingId)
                .build();

        return this.toBuilder()
                .state(SagaState.SHIPPING_REGISTERED)
                .shippingInfo(updatedShippingInfo)
                .shippingStepCompleted(true)
                .success(true)
                .build();
    }

    // 배송 생성 실패 시 호출
    public OrderContext shippingCreationFailed(String errorMessage) {
        return this.toBuilder()
                .state(SagaState.SHIPPING_REGISTRATION_FAILED)
                .shippingStepCompleted(false)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 배송 취소 시 호출
    public OrderContext shippingCancelled() {
        return this.toBuilder()
                .shippingInfo(null)
                .state(SagaState.SHIPPING_REGISTRATION_FAILED)
                .shippingStepCompleted(false)
                .build();
    }

    public OrderContext then(Function<OrderContext, OrderContext> nextStep) {
        return nextStep.apply(this);
    }
}
