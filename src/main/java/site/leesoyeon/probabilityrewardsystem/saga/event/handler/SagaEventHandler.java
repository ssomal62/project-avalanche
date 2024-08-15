package site.leesoyeon.probabilityrewardsystem.saga.event.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.saga.event.InventoryDeductionEvent;
import site.leesoyeon.probabilityrewardsystem.saga.event.OrderCreationEvent;

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
     * 재고 차감 이벤트를 처리합니다.
     *
     * @param event 재고 차감이 성공적으로 완료되었음을 나타내는 이벤트
     */
    @EventListener
    public void handleInventoryDeductionEvent(InventoryDeductionEvent event) {
        log.info("재고 이벤트가 성공적으로 처리되었습니다: {}", event.getContext());
        // 여기에 재고 차감 이벤트에 대한 추가 로직을 구현합니다.
    }

    /**
     * 주문 생성 이벤트를 처리합니다.
     *
     * @param event 주문 생성이 성공적으로 완료되었음을 나타내는 이벤트
     */
    @EventListener
    public void handleOrderCreationEvent(OrderCreationEvent event) {
        log.info("주문 이벤트가 성공적으로 처리되었습니다 {}", event.getContext());
        // 여기에 주문 생성 이벤트에 대한 추가 로직을 구현합니다.
    }
}