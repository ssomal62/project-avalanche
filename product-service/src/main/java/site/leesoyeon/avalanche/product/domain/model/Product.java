package site.leesoyeon.avalanche.product.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.shared.enums.Rarity;

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
    private Integer stock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "probability_multiplier")
    private Double probabilityMultiplier;

    @Version
    @Column(nullable = false)
    private Long version;

    public double getEffectiveDropRate() {
        double baseRate = rarity.getBaseDropRate();
        return probabilityMultiplier != null ? baseRate * probabilityMultiplier : baseRate;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void reduceStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

}
