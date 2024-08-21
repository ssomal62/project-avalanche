package site.leesoyeon.avalanche.order.saga.step;

public interface SagaStep<T> {
    T execute(T context);
    T compensate(T context);
}