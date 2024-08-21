package site.leesoyeon.avalanche.order.saga.coordinator;

public interface SagaCoordinator<T> {
    T execute(T context);
}