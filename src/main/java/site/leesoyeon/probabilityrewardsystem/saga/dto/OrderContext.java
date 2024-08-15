package site.leesoyeon.probabilityrewardsystem.saga.dto;

import lombok.Builder;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderItem;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingInfo;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

/**
 * {@code OrderContext}는 사가(Saga) 패턴에서 주문과 관련된 상태와 데이터를 저장하고 전달하는 데 사용됩니다.
 * 이 클래스는 주문 생성, 재고 차감, 배송 생성 등의 작업을 추적하고, 사가 상태와 연관된 다양한 메서드를 제공합니다.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>{@code userId} - 사용자 ID</li>
 *     <li>{@code orderId} - 주문 ID</li>
 *     <li>{@code orderStatus} - 주문 상태</li>
 *     <li>{@code usedPoint} - 사용된 포인트</li>
 *     <li>{@code orderItem} - 주문된 상품 정보</li>
 *     <li>{@code shippingInfo} - 배송 정보</li>
 *     <li>{@code createdAt} - 주문 생성 일시</li>
 *     <li>{@code state} - 사가 상태</li>
 *     <li>{@code success} - 작업 성공 여부</li>
 *     <li>{@code errorMessage} - 오류 메시지</li>
 * </ul>
 * <p>
 * 주요 메서드:
 * <ul>
 *     <li>사가의 성공 및 실패 상태 설정</li>
 *     <li>주문 생성, 주문 완료, 주문 취소 등의 상태 관리</li>
 *     <li>재고 차감 및 복구, 배송 생성 및 취소 등의 작업 관리</li>
 * </ul>
 */
@Builder(toBuilder = true)
public record OrderContext(
        UUID userId,
        UUID orderId,
        OrderStatus orderStatus,
        Integer usedPoint,
        OrderItem orderItem,
        ShippingInfo shippingInfo,
        LocalDateTime createdAt,
        SagaState state,
        boolean outOfStock,
        boolean success,
        String errorMessage
) {

    // ====== 사가 상태 관리 메서드 ======

    // 사가 성공 시 호출
    public OrderContext completed() {
        return this.toBuilder()
                .success(true)
                .state(SagaState.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 사가 실패 시 호출
    public OrderContext fail(String errorMessage) {
        return this.toBuilder()
                .success(false)
                .errorMessage(errorMessage)
                .state(SagaState.FAILED)
                .build();
    }

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

    // ====== 주문 관리 메서드 ======

    // 주문 생성 시 호출
    public OrderContext orderCreated(UUID orderId) {
        return this.toBuilder()
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATED)
                .state(SagaState.ORDER_CREATED)
                .success(true)
                .build();
    }

    // 주문 완료 시 호출
    public OrderContext completeOrder() {
        return this.toBuilder()
                .orderStatus(OrderStatus.COMPLETED)
                .state(SagaState.ORDER_COMPLETED)
                .success(true)
                .build();
    }

    // 주문 취소 시 호출
    public OrderContext orderCancelled() {
        return this.toBuilder()
                .orderId(null)
                .orderStatus(OrderStatus.CANCELLED)
                .success(true)
                .build();
    }

    // ====== 재고 관리 메서드 ======

    // 재고 차감 성공 시 호출
    public OrderContext inventoryDeducted() {
        return this.toBuilder()
                .state(SagaState.INVENTORY_DEDUCTED)
                .success(true)
                .build();
    }

    // 재고부족으로 작업이 중단될 때 호출
    public OrderContext inventoryDepleted(String errorMessage) {
        return this.toBuilder()
                .state(SagaState.INVENTORY_DEPLETED)
                .outOfStock(true)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 재고 복구 시 호출
    public OrderContext inventoryRefunded() {
        return this.toBuilder()
                .state(SagaState.COMPENSATION_COMPLETED)
                .build();
    }

    // ====== 배송 관리 메서드 ======

    // 배송 생성 시 호출
    public OrderContext shippingCreated(UUID shippingId) {
        ShippingInfo updatedShippingInfo = this.shippingInfo().toBuilder()
                .shippingId(shippingId)
                .build();

        return this.toBuilder()
                .state(SagaState.SHIPPING_CREATED)
                .shippingInfo(updatedShippingInfo)
                .build();
    }

    // 배송 취소 시 호출
    public OrderContext shippingCancelled() {
        return this.toBuilder()
                .shippingInfo(null)
                .build();
    }

    public OrderContext then(Function<OrderContext, OrderContext> nextStep) {
        return nextStep.apply(this);
    }
}
