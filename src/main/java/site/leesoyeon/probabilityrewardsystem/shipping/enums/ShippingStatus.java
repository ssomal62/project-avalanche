package site.leesoyeon.probabilityrewardsystem.shipping.enums;

import lombok.Getter;

@Getter
public enum ShippingStatus {

    PREPARING("준비중"),
    SHIPPED("출고완료"),
    IN_TRANSIT("배송중"),
    DELIVERED("배송완료"),
    FAILED("배송실패"),
    RETURNED("반송됨");

    private final String description;

    ShippingStatus(String description) {
        this.description = description;
    }
}