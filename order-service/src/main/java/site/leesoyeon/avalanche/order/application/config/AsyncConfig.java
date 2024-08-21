package site.leesoyeon.avalanche.order.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

/**
 * 이 클래스는 Spring 애플리케이션에서 비동기 처리를 활성화하고,
 * 비동기 작업을 효율적으로 처리하기 위한 커스텀 스레드 풀 실행기를 설정하는 구성 클래스입니다.
 *
 * <p>비동기 작업이란, 메인 스레드와 별도로 실행되는 작업으로, 이를 통해 애플리케이션의 성능을
 * 향상시키고, 긴 작업이 메인 스레드를 차단하지 않도록 합니다. 이 클래스는 이를 위해
 * {@link ThreadPoolTaskExecutor}를 설정하여 비동기 작업에 사용할 스레드 풀의 크기, 큐 용량,
 * 스레드 이름 접두사 등을 지정합니다.</p>
 *
 * <p>주요 구성 요소:
 * <ul>
 *   <li>{@code CorePoolSize} - 기본 스레드 풀 크기, 즉 항상 유지되는 최소 스레드 수를 지정합니다.</li>
 *   <li>{@code MaxPoolSize} - 최대 스레드 풀 크기를 지정합니다. 이 크기만큼의 스레드가 동시에 실행될 수 있습니다.</li>
 *   <li>{@code QueueCapacity} - 작업 큐의 최대 수용량을 지정합니다. 대기 중인 작업의 수가 이 값을 초과하면 새로운 작업은 거부됩니다.</li>
 *   <li>{@code ThreadNamePrefix} - 스레드 이름의 접두사를 지정하여 스레드를 구분하기 쉽게 합니다.</li>
 * </ul>
 * </p>
 *
 * <p>이 설정은 @EnableAsync 어노테이션을 통해 비동기 메서드 호출을 활성화하며,
 * 애플리케이션 내에서 @Async 어노테이션이 있는 메서드를 호출할 때 사용됩니다.</p>
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}