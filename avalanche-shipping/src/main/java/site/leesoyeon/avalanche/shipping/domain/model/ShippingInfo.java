package site.leesoyeon.avalanche.shipping.domain.model;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ShippingInfo(
        UUID shippingId,
        String recipientName,
        String recipientPhone,
        String address,
        String detailedAddress,
        String zipCode,

        boolean success,
        String errorMessage,
        String state,
        boolean shippingStepCompleted
) {
    // 사가 실패 시 호출
    public ShippingInfo fail(String errorMessage) {
        return this.toBuilder()
                .success(false)
                .errorMessage(errorMessage)
                .state("FAILED")
                .build();
    }

    //===================================
    //           배송 관리 메서드
    //===================================

    // 배송 생성 시 호출
    public ShippingInfo shippingCreated(UUID shippingId) {
        return this.toBuilder()
                .state("SHIPPING_CREATED")
                .shippingStepCompleted(true)
                .build();
    }

    // 배송 취소 시 호출
    public ShippingInfo shippingCancelled() {
        return this.toBuilder()
                .shippingStepCompleted(false)
                .build();
    }
}
