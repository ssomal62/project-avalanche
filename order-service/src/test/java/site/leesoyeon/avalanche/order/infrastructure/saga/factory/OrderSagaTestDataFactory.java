package site.leesoyeon.avalanche.order.infrastructure.saga.factory;

import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;
import site.leesoyeon.avalanche.order.presentation.dto.ProductInfo;
import site.leesoyeon.avalanche.order.presentation.dto.ShippingInfo;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;

import java.util.UUID;

public class OrderSagaTestDataFactory {

    static UUID productId = UUID.fromString("fececf36-edb5-4003-9574-351bd3d2589f");
    static UUID userId = UUID.randomUUID();

    public static ProductInfo createDefaultProductInfo() {
        return ProductInfo.builder()
                .productId(productId)
                .productName("Gaming Mouse")
                .unitPrice(800)
                .build();
    }

    public static ProductInfo createExpensiveProductInfo() {
        return ProductInfo.builder()
                .productId(UUID.randomUUID())
                .productName("High-end Gaming PC")
                .unitPrice(200000)
                .build();
    }

    public static ShippingInfo createDefaultShippingInfo() {
        return ShippingInfo.builder()
                .recipientName("John Doe")
                .recipientPhone("010-1234-5678")
                .address("123 Main St")
                .detailedAddress("Apt 4B")
                .zipCode("12345")
                .build();
    }

    public static ShippingInfo createInternationalShippingInfo() {
        return ShippingInfo.builder()
                .recipientName("Jane Smith")
                .recipientPhone("+1-555-1234")
                .address("456 Oak Avenue, USA")
                .detailedAddress("Suite 789")
                .zipCode("90210")
                .build();
    }

    public static Order createDefaultOrder() {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .userId(userId)
                .usedPoints(1600)
                .status(OrderStatus.CREATED)
                .build();
    }

    public static Order createLargeOrder() {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .userId(userId)
                .usedPoints(100000)
                .status(OrderStatus.CREATED)
                .build();
    }

    public static Order createCompletedOrder() {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .userId(userId)
                .usedPoints(5000)
                .status(OrderStatus.COMPLETED)
                .build();
    }

    public static OrderRequest createSampleOrderRequest() {
        return OrderRequest.builder()
                .userId(userId)
                .quantity(10)
                .amount(2800)
                .activityType("USE_RAFFLE")
                .productInfo(createDefaultProductInfo())
                .shippingInfo(createDefaultShippingInfo())
                .build();
    }

    public static OrderRequest createFailureOrderRequest() {
        return OrderRequest.builder()
                .userId(userId)
                .quantity(0)
                .amount(0)
                .activityType("USE_RAFFLE")
                .productInfo(createDefaultProductInfo())
                .shippingInfo(createDefaultShippingInfo())
                .build();
    }

    public static OrderRequest createLargeQuantityOrderRequest() {
        return OrderRequest.builder()
                .userId(userId)
                .quantity(1000)
                .amount(800000)
                .activityType("USE_RAFFLE")
                .productInfo(createDefaultProductInfo())
                .shippingInfo(createDefaultShippingInfo())
                .build();
    }

    public static OrderRequest createInternationalOrderRequest() {
        return OrderRequest.builder()
                .userId(userId)
                .quantity(5)
                .amount(1000000)
                .activityType("USE_RAFFLE")
                .productInfo(createExpensiveProductInfo())
                .shippingInfo(createInternationalShippingInfo())
                .build();
    }

    public static OrderRequest createZeroPointOrderRequest() {
        return OrderRequest.builder()
                .userId(userId)
                .quantity(1)
                .amount(0)
                .activityType("FREE_GIFT")
                .productInfo(createDefaultProductInfo())
                .shippingInfo(createDefaultShippingInfo())
                .build();
    }
}
