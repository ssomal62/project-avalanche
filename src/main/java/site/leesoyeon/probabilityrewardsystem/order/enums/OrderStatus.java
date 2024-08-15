package site.leesoyeon.probabilityrewardsystem.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PAYMENT_PENDING("결제 대기 중"),
    PAYMENT_FAILED("결제 실패"),
    PAYMENT_CONFIRMED("결제 확인됨"),
    CREATED("주문 생성"),
    PREPARING("상품 준비 중"),
    SHIPPED("출고 완료"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배송 완료"),
    FAILED("배송 실패"),
    RETURNED("반송됨"),
    COMPLETED("주문 완료"),
    CANCELLED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;

    }
}
