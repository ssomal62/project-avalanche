package site.leesoyeon.avalanche.order.infrastructure.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.leesoyeon.avalanche.order.presentation.dto.ProductInfo;
import site.leesoyeon.avalanche.order.presentation.dto.ShippingInfo;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandStatus;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandType;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaState {

    private UUID orderId;
    private UUID userId;
    private UUID pointId;
    private int quantity;
    private Integer amount;
    private String activityType;
    private ProductInfo productInfo;
    private UUID shippingId;
    private ShippingInfo shippingInfo;
    private OrderSagaStatus status;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant lastUpdated = Instant.now();

    @Builder.Default
    private Map<CommandType, CommandStatus> commandStatuses = new EnumMap<>(CommandType.class);

    @Builder.Default
    private Map<CommandType, CommandStatus> compensationStatuses = new EnumMap<>(CommandType.class);


    boolean hasFailedCommands() {
        return commandStatuses.values().stream()
                .anyMatch(status -> status == CommandStatus.FAILED);
    }

    boolean allCommandsResponded() {
        return this.getCommandStatuses().size() >= CommandType.values().length - 1;
    }

    boolean allCompensationsCompleted() {
        long successfulCommands = this.getCommandStatuses().values().stream()
                .filter(status -> status == CommandStatus.SUCCESS)
                .count();

        long successfulCompensations = this.getCompensationStatuses().values().stream()
                .filter(status -> status == CommandStatus.SUCCESS)
                .count();

        return successfulCompensations == successfulCommands;
    }

    public synchronized void updateCommandStatus(CommandType commandType, CommandStatus status) {
        commandStatuses.put(commandType, status);
        this.lastUpdated = Instant.now();
    }

    public synchronized void updateCompensationStatus(CommandType commandType, CommandStatus status) {
        compensationStatuses.put(commandType, status);
        this.lastUpdated = Instant.now();
    }

    public void updateShippingId(UUID shippingId) {
        this.shippingId = shippingId;
    }

    public void updatePointId(UUID pointId) {
        this.pointId = pointId;
    }

    public void updateStatus(OrderSagaStatus newStatus) {
        this.status = newStatus;
        this.lastUpdated = Instant.now();
    }
}