package site.leesoyeon.avalanche.shipping.domain.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import site.leesoyeon.avalanche.shipping.shared.enums.ShippingStatus;


import java.util.UUID;

@Entity
@Table(name = "shipping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Shipping extends BaseTimeEntity {

    @Id
    @UuidGenerator
    @Column(name = "shipping_id", updatable = false, nullable = false)
    private UUID shippingId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @NotBlank(message = "Recipient name must not be blank")
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String address;

    @Column(name = "detailed_address")
    private String detailedAddress;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status;

    public void updateStatus(ShippingStatus newStatus) {
        this.status = newStatus;
    }

    public void updateRecipientInfo(String newName, String newPhone) {
        this.recipientName = newName;
        this.recipientPhone = newPhone;
    }

    public void updateAddress(String newAddress, String newDetailedAddress, String newZipCode) {
        this.address = newAddress;
        this.detailedAddress = newDetailedAddress;
        this.zipCode = newZipCode;
    }

    public String getStatusDescription() {
        return this.status.getDescription();
    }
}
