package site.leesoyeon.avalanche.point.infrastructure.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 이 클래스는 AOP(Aspect-Oriented Programming)를 사용하여 메서드 실행 전후로 로깅을 수행하는 로깅 관점을 정의합니다.
 *
 * <p>로깅 관점은 애플리케이션의 특정 관심사를 모듈화하여 관리할 수 있게 해주며, 이 클래스는 특히 서비스 레이어의 메서드 실행 시간과
 * 호출 정보를 로깅하는 데 사용됩니다. 이를 통해 성능 모니터링과 디버깅에 도움을 줄 수 있습니다.</p>
 *
 * <p>주요 기능:
 * <ul>
 *   <li>{@code @Around} - 지정된 패턴과 일치하는 메서드 실행 전후에 코드 블록을 실행합니다. 이 경우, 서비스 레이어의 모든 메서드 실행을 감싸고 있습니다.</li>
 *   <li>{@code logMethodExecution} - 메서드 실행 전후에 실행 시간을 측정하고, 메서드 이름과 실행 시간을 로깅합니다.</li>
 *   <li>로그는 SLF4J를 사용하여 기록되며, 메서드 실행 전에 시작 로그를, 실행 후에 완료 로그를 기록합니다.</li>
 * </ul>
 * </p>
 *
 * <p>이 설정을 통해 애플리케이션 내의 서비스 레이어에서 성능 문제를 파악하거나 특정 메서드 호출의 빈도와 실행 시간을 추적할 수 있습니다.</p>
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* site.leesoyeon.avalanche.point..service.*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Executing method: {}", methodName);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        logger.info("Executed method: {} in {} ms", methodName, executionTime);
        return result;
    }
}