package site.leesoyeon.avalanche.order.saga.state;

import lombok.Getter;

@Getter
public enum SagaState {

    ORDER_PENDING("주문이 시작되었으며, 생성 대기 중입니다."),
    ORDER_CREATED("주문이 성공적으로 생성되었습니다."),
    ORDER_CREATION_FAILED("주문 생성에 실패했습니다."),

    POINT_DEDUCTION_PENDING("포인트 차감이 대기 중입니다."),
    POINT_DEDUCTED("포인트가 성공적으로 차감되었습니다."),
    POINT_DEDUCTION_FAILED("포인트 차감에 실패했습니다."),

    INVENTORY_DEDUCTION_PENDING("재고 차감이 대기 중입니다."),
    INVENTORY_DEDUCTED("재고가 성공적으로 차감되었습니다."),
    INVENTORY_DEDUCTION_FAILED("재고 차감에 실패했습니다."),

    SHIPPING_REGISTRATION_PENDING("배송지 등록이 대기 중입니다."),
    SHIPPING_REGISTERED("배송지 정보가 성공적으로 등록되었습니다."),
    SHIPPING_REGISTRATION_FAILED("배송지 등록에 실패했습니다."),

    ORDER_FINALIZATION_PENDING("주문 완료가 대기 중입니다."),
    ORDER_FINALIZED("주문이 성공적으로 완료되었습니다."),
    ORDER_FINALIZATION_FAILED("주문 완료에 실패했습니다."),

    COMPENSATION_IN_PROGRESS("보상 트랜잭션이 진행 중입니다."),
    COMPENSATION_COMPLETED("보상 트랜잭션이 완료되었습니다."),
    COMPENSATION_FAILED("보상 트랜잭션이 실패하였습니다.");

    private final String description;

    SagaState(String description) {
        this.description = description;
    }
}