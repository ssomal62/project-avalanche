package site.leesoyeon.probabilityrewardsystem.saga.coordinator;

public interface SagaCoordinator<T> {
    T execute(T context);
}