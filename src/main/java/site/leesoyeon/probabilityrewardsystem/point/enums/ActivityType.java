package site.leesoyeon.probabilityrewardsystem.point.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    EARN_LOGIN("로그인 포인트 적립"),
    EARN_EVENT("이벤트 포인트 적립"),
    EARN_PURCHASE("구매 포인트 적립"),
    USE_RAFFLE("추첨 참여 포인트 사용"),
    EXPIRE("포인트 만료"),
    REFUND("포인트 환불"),
    ADJUST_MANUAL("수동 포인트 조정"),
    DEDUCT_ORDER("주문 시 포인트 차감"),
    COMPENSATE_ORDER("주문 취소 포인트 보상");

    private final String description;

    ActivityType(String description) {
        this.description = description;
    }
}