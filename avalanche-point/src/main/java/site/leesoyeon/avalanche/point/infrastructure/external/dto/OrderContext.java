package site.leesoyeon.avalanche.point.infrastructure.external.dto;

import lombok.Builder;
import site.leesoyeon.avalanche.point.domain.model.PointTransactionInfo;

import java.util.UUID;

@Builder(toBuilder = true)
public record OrderContext(
        UUID userId,
        UUID orderId,
        Integer quantity,
        PointTransactionInfo transactionInfo,
        ProductInfo productInfo,
        SagaState state,

        boolean pointStepCompleted,
        boolean success,
        String errorMessage
) {

    //===================================
    //          포인트 관리 메서드
    //===================================

    // 포인트 차감 성공 시 호출
    public OrderContext pointDeducted(UUID transactionId, String description) {
        PointTransactionInfo updatedTransactionInfo = this.transactionInfo().toBuilder()
                .transactionId(transactionId)
                .description(description)
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
}

enum SagaState {
    POINT_DEDUCTION_PENDING("포인트 차감이 대기 중입니다."),
    POINT_DEDUCTED("포인트가 성공적으로 차감되었습니다."),
    POINT_DEDUCTION_FAILED("포인트 차감에 실패했습니다.");

    private final String description;
    SagaState(String description) {
        this.description = description;
    }
}

