package site.leesoyeon.probabilityrewardsystem.saga.state;

import lombok.Getter;

@Getter
public enum SagaState {
    STARTED("사가 시작"),
    COMPLETED("사가 완료"),
    FAILED("사가 실패"),
    POINT_DEDUCTED("포인트 차감 완료"),
    POINT_REFUNDED("포인트 환불 완료"),
    INVENTORY_DEDUCTED("재고 차감 완료"),
    INVENTORY_DEPLETED("재고 부족"),
    ORDER_CREATED("주문 생성 완료"),
    ORDER_COMPLETED("주문 완료"),
    ORDER_CANCELLED("주문 취소"),
    SHIPPING_CREATED("배송 생성 완료"),
    COMPENSATION_IN_PROGRESS("보상 작업 진행 중"),
    COMPENSATION_COMPLETED("보상 작업 완료"),
    COMPENSATION_FAILED("보상 작업 실패");

    private final String description;

    SagaState(String description) {
        this.description = description;
    }
}