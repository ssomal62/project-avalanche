package site.leesoyeon.avalanche.product.shared.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {

    AVAILABLE("판매중", "눈송이 상점에서 구매 가능한 상태입니다."),
    UPCOMING("출시 예정", "곧 눈송이 상점에 등장할 예정입니다."),
    SOLD_OUT("품절", "현재 재고가 모두 소진되었습니다."),
    DISCONTINUED("판매 중단", "더 이상 판매되지 않는 상품입니다."),
    LIMITED_TIME("한정 판매", "특정 기간 동안만 구매 가능한 상품입니다."),
    SEASONAL("시즌 한정", "특정 시즌에만 구매 가능한 상품입니다."),
    MAINTENANCE("점검 중", "상품 정보 업데이트 중입니다.");

    private final String displayName;
    private final String description;

    ProductStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}