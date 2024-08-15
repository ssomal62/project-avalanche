package site.leesoyeon.probabilityrewardsystem.saga.event.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.saga.event.InventoryDeductionEvent;
import site.leesoyeon.probabilityrewardsystem.saga.event.OrderCreationEvent;
import site.leesoyeon.probabilityrewardsystem.saga.event.PointDeductionEvent;
import site.leesoyeon.probabilityrewardsystem.saga.event.StepCompletedEvent;

/**
 * {@code SagaEventHandler} 클래스는 사가(Saga) 패턴에서 발생하는 다양한 이벤트를 처리하는 역할을 합니다.
 * 이 클래스는 각 이벤트를 감지하고, 비동기적으로 후속 작업을 처리함으로써 메인 비즈니스 로직의 성능을 유지하고
 * 시스템의 확장성을 높이는 데 기여합니다.
 *
 * <p>이벤트 핸들러 메서드는 주로 다음과 같은 용도로 사용됩니다:</p>
 * <ul>
 *     <li>주요 비즈니스 로직이 완료된 후, 즉시 처리되지 않아도 되는 작업을 비동기적으로 수행.</li>
 *     <li>재고 차감, 주문 생성 등 특정 작업이 성공적으로 완료되었음을 기록하거나,
 *     다른 시스템과의 연동을 통해 후속 작업을 처리.</li>
 *     <li>이벤트 기반 아키텍처를 통해 시스템의 모듈화를 지원하고, 코드의 유지보수성을 향상.</li>
 * </ul>
 *
 * <p>이벤트 처리 예시:</p>
 * <ul>
 *     <li>포인트 차감 완료 후 고객에게 알림을 전송.</li>
 *     <li>재고 차감 후 관리자에게 재고 부족 경고를 발송.</li>
 *     <li>주문 생성 후 확인 이메일을 발송.</li>
 * </ul>
 */
@Slf4j
@Component
public class SagaEventHandler {

    /**
     * 주문 생성 이벤트를 처리합니다.
     *
     * @param event 주문 생성이 성공적으로 완료되었음을 나타내는 이벤트
     */
    @EventListener
    public void handleOrderCreationEvent(OrderCreationEvent event) {
       log.info("주문 등록 완료로 후속 작업을 진행합니다. : {}", event.getContext());


       log.info("후속 작업을 모두 완료하였습니다.");
    }

    @EventListener
    public void handleInventoryDeductionEvent(InventoryDeductionEvent event) {}

    @EventListener
    public void handlePointDeductionEvent(PointDeductionEvent event) {}

    @EventListener
    public void handleStepCompletedEvent(StepCompletedEvent event) {
        log.info("Step completed: {}, Saga State: {}, Success: {}",
                event.getStepName(), event.getSagaState(), event.isSuccess());
    }
}