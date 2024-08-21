package site.leesoyeon.avalanche.shipping.infrastructure.external.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.shipping.domain.model.ShippingInfo;

import java.util.UUID;

@Builder(toBuilder = true)
public record OrderContext(
        UUID userId,
        UUID orderId,
        ShippingInfo shippingInfo,
        SagaState state,

        boolean shippingStepCompleted,

        boolean success,
        String errorMessage
) {

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
                .shippingStepCompleted(true)
                .shippingInfo(updatedShippingInfo)
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
                .state(SagaState.SHIPPING_REGISTRATION_FAILED)
                .shippingStepCompleted(false)
                .shippingInfo(null)
                .build();
    }
}

enum SagaState {

    SHIPPING_REGISTRATION_PENDING("배송지 등록이 대기 중입니다."),
    SHIPPING_REGISTERED("배송지 정보가 성공적으로 등록되었습니다."),
    SHIPPING_REGISTRATION_FAILED("배송지 등록에 실패했습니다.");

    private final String description;
    SagaState(String description) {
        this.description = description;
    }
}
