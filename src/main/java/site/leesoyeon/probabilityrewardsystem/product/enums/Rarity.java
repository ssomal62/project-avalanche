package site.leesoyeon.probabilityrewardsystem.product.enums;

import lombok.Getter;

@Getter
public enum Rarity {

    COMMON("흔한 눈송이", 60.0),
    RARE("눈꽃 결정", 25.0),
    EPIC("얼음 조각", 10.0),
    LEGENDARY("오로라의 빛", 4.0),
    MYTHIC("엘사의 선물", 1.0);

    private final String description;
    private final double baseDropRate;

    Rarity(String description, double baseDropRate) {
        this.description = description;
        this.baseDropRate = baseDropRate;
    }

    public static Rarity fromString(String rarity) {
        return valueOf(rarity.toUpperCase());
    }
}
