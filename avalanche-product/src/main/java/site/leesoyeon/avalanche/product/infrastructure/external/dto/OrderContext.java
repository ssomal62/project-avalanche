package site.leesoyeon.avalanche.product.infrastructure.external.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.product.domain.model.ProductInfo;

import java.util.UUID;

@Builder(toBuilder = true)
public record OrderContext(
        UUID userId,
        UUID orderId,
        Integer quantity,
        ProductInfo productInfo,
        SagaState state,

        boolean inventoryStepCompleted,

        boolean success,
        String errorMessage
) {

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

    // 재고 처리 실패 시 호출
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

}

enum SagaState {

    INVENTORY_DEDUCTION_PENDING("재고 차감이 대기 중입니다."),
    INVENTORY_DEDUCTED("재고가 성공적으로 차감되었습니다."),
    INVENTORY_DEDUCTION_FAILED("재고 차감에 실패했습니다.");

    private final String description;
    SagaState(String description) {
        this.description = description;
    }
}
