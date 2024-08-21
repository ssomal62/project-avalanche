package site.leesoyeon.avalanche.product.domain.model;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ProductInfo(
        UUID productId,
        String name,
        Integer unitPrice,
        Integer quantity,

        boolean success,
        String errorMessage,
        String state,
        boolean inventoryStepCompleted

) {
    // 사가 실패 시 호출
    public ProductInfo fail(String errorMessage) {
        return this.toBuilder()
                .success(false)
                .errorMessage(errorMessage)
                .state("FAILED")
                .build();
    }

    //===================================
    //           재고 관리 메서드
    //===================================

    // 재고 차감 성공 시 호출
    public ProductInfo inventoryDeducted() {
        return this.toBuilder()
                .state("INVENTORY_DEDUCTED")
                .inventoryStepCompleted(true)
                .success(true)
                .build();
    }

    // 재고부족으로 작업이 중단될 때 호출
    public ProductInfo inventoryDepleted(String errorMessage) {
        return this.toBuilder()
                .state("INVENTORY_DEPLETED")
                .success(false)
                .inventoryStepCompleted(false)
                .errorMessage(errorMessage)
                .build();
    }

    // 재고 복구 시 호출
    public ProductInfo inventoryRefunded() {
        return this.toBuilder()
                .inventoryStepCompleted(false)
                .build();
    }

}