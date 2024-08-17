package site.leesoyeon.avalanche.order.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

import java.util.UUID;

@Entity
@Table(name = "Orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order extends BaseTimeEntity {

    @Id
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "used_points")
    private Integer usedPoints;

    @Column(name = "shipping_id")
    private UUID shippingId;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Version
    @Column(nullable = false)
    private Long version;

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public void updateShippingId(UUID newShippingId) {
        this.shippingId = newShippingId;
        this.status = OrderStatus.CREATED;
    }

    public void updateStatusBasedOnShipping(String shippingStatus) {
        this.status = OrderStatus.valueOf(shippingStatus);
    }
}

