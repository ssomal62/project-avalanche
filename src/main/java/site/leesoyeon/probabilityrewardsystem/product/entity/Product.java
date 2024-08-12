package site.leesoyeon.probabilityrewardsystem.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import site.leesoyeon.probabilityrewardsystem.common.BaseTimeEntity;
import site.leesoyeon.probabilityrewardsystem.product.enums.ProductStatus;
import site.leesoyeon.probabilityrewardsystem.product.enums.Rarity;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id
    @UuidGenerator
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "probability_multiplier")
    private Double probabilityMultiplier;

    @Builder
    public Product(String name, String description, Rarity rarity, BigDecimal price,
                   String categoryName, Integer quantity, String imageUrl, Double probabilityMultiplier) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.price = price;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.probabilityMultiplier = probabilityMultiplier;
    }

    public void update(String name, String description, Rarity rarity, BigDecimal price,
                       String categoryName, Integer quantity, String imageUrl, Double probabilityMultiplier) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.price = price;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.probabilityMultiplier = probabilityMultiplier;
    }

    public double getEffectiveDropRate() {
        double baseRate = rarity.getBaseDropRate();
        return probabilityMultiplier != null ? baseRate * probabilityMultiplier : baseRate;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}
