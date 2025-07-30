package site.leesoyeon.avalanche.order.infrastructure.saga;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.infrastructure.exception.OrderSagaException;
import site.leesoyeon.avalanche.order.infrastructure.messaging.OrderSagaProducer;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandStatus;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.CommandType;
import site.leesoyeon.avalanche.order.infrastructure.saga.enums.OrderSagaStatus;
import site.leesoyeon.avalanche.order.infrastructure.event.OrderCancelledEvent;
import site.leesoyeon.avalanche.order.infrastructure.event.OrderCompletedEvent;
import site.leesoyeon.avalanche.order.infrastructure.saga.factory.OrderSagaTestDataFactory;
import site.leesoyeon.avalanche.order.infrastructure.saga.repository.SagaStateRepository;
import site.leesoyeon.avalanche.order.infrastructure.saga.state.OrderSagaState;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(MockitoExtension.class)
class OrderSagaManagerTest {

    @Mock
    private SagaStateRepository sagaStateRepository;
    @Mock
    private OrderSagaProducer orderSagaProducer;
    @Mock
    private RedisTemplate<String, OrderSagaState> redisTemplate;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rLock;
    @Mock
    private ValueOperations<String, OrderSagaState> valueOperations;

    private OrderSagaManager orderSagaManager;

    private Order order;
    private OrderRequest orderRequest;
    private OrderSagaState orderSagaState;

    private static final Duration ORDER_STATE_TTL = Duration.ofMinutes(10);
    private static final int MAX_RETRY_COUNT = 3;

    @BeforeEach
    void setUp() {
        orderSagaManager = new OrderSagaManager(orderSagaProducer, sagaStateRepository, redisTemplate, eventPublisher, redissonClient);
        order = OrderSagaTestDataFactory.createDefaultOrder();
        orderRequest = OrderSagaTestDataFactory.createSampleOrderRequest();
        orderSagaState = OrderSagaState.createInitialState(order, orderRequest);
    }

    @Test
    @DisplayName("사가 시작 - 명령이 정상적으로 전송됨")
    void startSaga_shouldSendCommands() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        orderSagaManager.startSaga(order, orderRequest);

        // Then
        verify(valueOperations).set(eq("saga:" + order.getOrderId()), any(OrderSagaState.class));
        verify(orderSagaProducer).sendCheckStockCommand(eq(order.getOrderId()), anyString(), eq(orderRequest.quantity()));
        verify(orderSagaProducer).sendApplyPointsCommand(eq(order.getOrderId()), eq(orderRequest.userId()), eq(orderRequest.amount()), eq(orderRequest.activityType()), anyString());
        verify(orderSagaProducer).sendPrepareShippingCommand(eq(order.getOrderId()), eq(orderRequest.shippingInfo()));
    }

    @Test
    @DisplayName("명령 응답 처리 - 사가 상태 업데이트 및 다음 작업 수행")
    void handleCommandResponse_shouldUpdateStateAndProcessNextAction() throws InterruptedException {
        // Given
        when(sagaStateRepository.getSagaState(order.getOrderId())).thenReturn(Optional.of(orderSagaState));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handleCommandResponse(order.getOrderId(), CommandType.CHECK_STOCK, true);

        // Then
        verify(sagaStateRepository).saveSagaState(eq(order.getOrderId()), any(OrderSagaState.class), any());
        verify(rLock).unlock();
    }

    @Test
    @DisplayName("명령 실패 후 보상 트랜잭션 호출 확인")
    void handleCommandResponse_shouldTriggerCompensation() throws InterruptedException {
        // Given
        UUID orderId = order.getOrderId();
        OrderSagaState sagaState = OrderSagaState.createInitialState(order, orderRequest);

        // 사가 상태를 미리 세팅: APPLY_POINTS와 PREPARE_SHIPPING은 성공
        sagaState.updateCommandStatus(CommandType.APPLY_POINTS, CommandStatus.SUCCESS);
        sagaState.updateCommandStatus(CommandType.PREPARE_SHIPPING, CommandStatus.SUCCESS);

        // 포인트 ID와 배송 ID를 설정하여 보상 트랜잭션이 발동되도록 설정
        sagaState.updatePointId(UUID.randomUUID());  // 포인트 ID 설정
        sagaState.updateShippingId(UUID.randomUUID());  // 배송 ID 설정

        // 초기 상태를 설정
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(sagaState));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When: CHECK_STOCK 명령이 실패했음을 시뮬레이션
        orderSagaManager.handleCommandResponse(orderId, CommandType.CHECK_STOCK, false);

        // Then: 보상 트랜잭션이 호출되었는지 확인
        verify(orderSagaProducer).sendRefundPointCommand(eq(orderId), any(), anyInt());
        verify(orderSagaProducer).sendCancelShippingCommand(eq(orderId), any());
    }


    @Test
    @DisplayName("보상 트랜잭션 성공 시 사가 상태 전이 확인")
    void handleCompensationResponse_shouldUpdateSagaToCancelled() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderSagaState sagaState = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.COMPENSATING)
                .build();

        // 이미 APPLY_POINTS와 PREPARE_SHIPPING 커맨드가 성공한 상태로 설정
        sagaState.updateCommandStatus(CommandType.CHECK_STOCK, CommandStatus.FAILED);
        sagaState.updateCommandStatus(CommandType.APPLY_POINTS, CommandStatus.SUCCESS);
        sagaState.updateCommandStatus(CommandType.PREPARE_SHIPPING, CommandStatus.SUCCESS);

        // 초기 Mock 설정
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(sagaState));
        when(sagaStateRepository.updateSagaStatus(eq(orderId), eq(OrderSagaStatus.CANCELLED), any(), anyInt())).thenReturn(true);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When: APPLY_POINTS와 PREPARE_SHIPPING의 보상 트랜잭션이 성공했다고 응답
        orderSagaManager.handleCommandResponse(orderId, CommandType.APPLY_POINTS, true);
        orderSagaManager.handleCommandResponse(orderId, CommandType.PREPARE_SHIPPING, true);

        // Then: 사가 상태가 CANCELLED로 업데이트되었는지 확인
        verify(sagaStateRepository).updateSagaStatus(eq(orderId), eq(OrderSagaStatus.CANCELLED), any(), anyInt());

        // Then: 주문 취소 이벤트가 발행되었는지 확인
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }


    @Test
    @DisplayName("사가 완료 - 이벤트 발행")
    void completeSaga_shouldPublishEventWhenSagaIsCompleted() {
        // Given
        when(sagaStateRepository.updateSagaStatus(order.getOrderId(), OrderSagaStatus.COMPLETED, ORDER_STATE_TTL, MAX_RETRY_COUNT)).thenReturn(true);

        // When
        orderSagaManager.completeSaga(order.getOrderId());

        // Then
        verify(eventPublisher).publishEvent(any(OrderCompletedEvent.class));
    }

    @Test
    @DisplayName("사가 취소 - 이벤트 발행")
    void cancelOrder_shouldPublishEventWhenSagaIsCancelled() {
        // Given
        when(sagaStateRepository.updateSagaStatus(order.getOrderId(), OrderSagaStatus.CANCELLED, ORDER_STATE_TTL, MAX_RETRY_COUNT)).thenReturn(true);

        // When
        orderSagaManager.cancelOrder(order.getOrderId());

        // Then
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    @DisplayName("타임아웃 발생 시 PENDING 상태를 FAILED로 변경")
    void initiateCompensationForTimeout_shouldChangePendingToFailed() throws InterruptedException {
        // Given: 초기 상태 설정
        UUID orderId = UUID.randomUUID();
        EnumMap<CommandType, CommandStatus> commandStatuses = new EnumMap<>(CommandType.class);
        commandStatuses.put(CommandType.CHECK_STOCK, CommandStatus.PENDING);
        commandStatuses.put(CommandType.APPLY_POINTS, CommandStatus.SUCCESS);
        commandStatuses.put(CommandType.PREPARE_SHIPPING, CommandStatus.SUCCESS);

        OrderSagaState orderSagaState = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.STARTED)
                .commandStatuses(commandStatuses)
                .build();

        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(orderSagaState));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When: 타임아웃으로 인한 처리를 시작
        orderSagaManager.initiateCompensationForTimeout(orderId);

        // Then: PENDING 상태가 FAILED로 변경되었는지 확인
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository, atLeastOnce()).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(CommandStatus.FAILED, capturedState.getCommandStatuses().get(CommandType.CHECK_STOCK));
        assertEquals(CommandStatus.FAILED, capturedState.getCommandStatuses().get(CommandType.TIMEOUT));

    }

    @Test
    @DisplayName("락 획득 실패 시 재시도 후 예외 발생")
    void executeWithLock_shouldRetryAndThrowExceptionOnFailure() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(false);

        // When & Then
        OrderSagaException exception = assertThrows(OrderSagaException.class, () -> {
            orderSagaManager.executeWithLock(orderId, () -> {
                // 이 부분은 실행되지 않아야 합니다.
                fail("This code should not be executed");
                return null;
            });
        });

        assertEquals("최대 재시도 횟수 초과: orderId=" + orderId, exception.getMessage());

        // Verify that tryLock was called MAX_RETRY_COUNT times
        verify(rLock, times(MAX_RETRY_COUNT)).tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("재고 확인 처리")
    void handleStockChecked() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(orderSagaState));

        // When
        orderSagaManager.handleStockChecked(orderId, true);

        // Then
        verify(sagaStateRepository).saveSagaState(eq(orderId), argThat(state ->
                state.getCommandStatuses().get(CommandType.CHECK_STOCK) == CommandStatus.SUCCESS
        ), any());
        verify(rLock).unlock();
    }

    @Test
    @DisplayName("포인트 적용 처리")
    void handlePointApplied() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(orderSagaState));

        // When
        orderSagaManager.handlePointApplied(orderId, pointId, true);

        // Then
        verify(sagaStateRepository, times(2)).saveSagaState(eq(orderId), argThat(state ->
                state.getPointId().equals(pointId) &&
                        state.getCommandStatuses().get(CommandType.APPLY_POINTS) == CommandStatus.SUCCESS
        ), any());
        verify(rLock, times(2)).unlock();
    }

    @Test
    @DisplayName("배송 준비 처리")
    void handleShippingPrepared() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID shippingId = UUID.randomUUID();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(orderSagaState));

        // When
        orderSagaManager.handleShippingPrepared(orderId, shippingId, true);

        // Then
        verify(sagaStateRepository, times(2)).saveSagaState(eq(orderId), argThat(state ->
                state.getShippingId().equals(shippingId) &&
                        state.getCommandStatuses().get(CommandType.PREPARE_SHIPPING) == CommandStatus.SUCCESS
        ), any());
        verify(rLock, times(2)).unlock();
    }

    @Test
    @DisplayName("포인트 적용 성공 시 포인트 ID 업데이트 및 상태 변경 확인")
    void handlePointApplied_shouldUpdatePointIdAndStatus() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID pointId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.createInitialState(order, orderRequest);
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handlePointApplied(orderId, pointId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository, atLeastOnce()).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(pointId, capturedState.getPointId());
        assertEquals(CommandStatus.SUCCESS, capturedState.getCommandStatuses().get(CommandType.APPLY_POINTS));
    }

    @Test
    @DisplayName("배송 준비 성공 시 배송 ID 업데이트 및 상태 변경 확인")
    void handleShippingPrepared_shouldUpdateShippingIdAndStatus() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID shippingId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.createInitialState(order, orderRequest);
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handleShippingPrepared(orderId, shippingId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository, atLeastOnce()).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(shippingId, capturedState.getShippingId());
        assertEquals(CommandStatus.SUCCESS, capturedState.getCommandStatuses().get(CommandType.PREPARE_SHIPPING));
    }

    @Test
    @DisplayName("재고 해제 명령 처리 확인")
    void handleStockReleased_shouldUpdateCompensationStatus() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.COMPENSATING)
                .build();
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handleStockReleased(orderId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(CommandStatus.SUCCESS, capturedState.getCompensationStatuses().get(CommandType.CHECK_STOCK));
    }

    @Test
    @DisplayName("포인트 환불 처리 확인")
    void handlePointsRefunded_shouldUpdateCompensationStatus() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.COMPENSATING)
                .build();
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handlePointsRefunded(orderId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(CommandStatus.SUCCESS, capturedState.getCompensationStatuses().get(CommandType.APPLY_POINTS));
    }

    @Test
    @DisplayName("배송 취소 처리 확인")
    void handleShippingCancelled_shouldUpdateCompensationStatus() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.COMPENSATING)
                .build();
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handleShippingCancelled(orderId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(CommandStatus.SUCCESS, capturedState.getCompensationStatuses().get(CommandType.PREPARE_SHIPPING));
    }

    @Test
    @DisplayName("재고 해제 처리 확인")
    void handleStockReleased() throws InterruptedException {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderSagaState state = OrderSagaState.builder()
                .orderId(orderId)
                .status(OrderSagaStatus.COMPENSATING)
                .build();
        when(sagaStateRepository.getSagaState(orderId)).thenReturn(Optional.of(state));
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        orderSagaManager.handleStockReleased(orderId, true);

        // Then
        ArgumentCaptor<OrderSagaState> stateCaptor = ArgumentCaptor.forClass(OrderSagaState.class);
        verify(sagaStateRepository).saveSagaState(eq(orderId), stateCaptor.capture(), any());

        OrderSagaState capturedState = stateCaptor.getValue();
        assertEquals(CommandStatus.SUCCESS, capturedState.getCompensationStatuses().get(CommandType.CHECK_STOCK));
        verify(rLock).unlock();
    }
}

