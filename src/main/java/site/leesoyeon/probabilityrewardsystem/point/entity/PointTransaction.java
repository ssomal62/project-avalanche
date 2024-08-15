package site.leesoyeon.probabilityrewardsystem.point.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import site.leesoyeon.probabilityrewardsystem.common.BaseTimeEntity;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "point_transactions")
public class PointTransaction extends BaseTimeEntity {

    @Id
    @UuidGenerator
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Version
    @Column(nullable = false)
    private Long version;

    public void cancel() {
        if (this.isCancelled) {
            throw new IllegalStateException("이미 취소된 트랜잭션입니다.");
        }
        this.isCancelled = true;
    }

    public void updateBalance(Integer currentPoint) {
        this.balance += currentPoint;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void updateProductId(UUID productId) {
        this.productId = productId;
    }
}
