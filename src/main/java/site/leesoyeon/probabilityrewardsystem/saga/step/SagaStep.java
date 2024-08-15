package site.leesoyeon.probabilityrewardsystem.saga.step;

public interface SagaStep<T> {
    T execute(T context);
    T compensate(T context);
}