package site.leesoyeon.probabilityrewardsystem.saga.event;


import lombok.Builder;
import lombok.Getter;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaEventType;
import site.leesoyeon.probabilityrewardsystem.saga.state.SagaState;

import java.time.Instant;
import java.util.UUID;

@Getter
public class StepCompletedEvent implements SagaEvent{
    private final UUID eventId;
    private final String stepName;
    private final SagaState sagaState;
    private final UUID sagaId;
    private final Instant timestamp;
    private final boolean success;
    private final String details;

    @Builder
    public StepCompletedEvent(String stepName, SagaState sagaState, UUID sagaId, boolean success, String details) {
        this.eventId = UUID.randomUUID();
        this.stepName = stepName;
        this.sagaState = sagaState;
        this.sagaId = sagaId;
        this.timestamp = Instant.now();
        this.success = success;
        this.details = details;
    }

    @Override
    public String toString() {
        return "StepCompletedEvent{" +
                "eventId=" + eventId +
                ", stepName='" + stepName + '\'' +
                ", sagaState=" + sagaState +
                ", sagaId=" + sagaId +
                ", timestamp=" + timestamp +
                ", success=" + success +
                ", details='" + details + '\'' +
                '}';
    }

    @Override
    public SagaEventType getEventType() {
        return null;
    }

    @Override
    public OrderContext getContext() {
        return null;
    }
}