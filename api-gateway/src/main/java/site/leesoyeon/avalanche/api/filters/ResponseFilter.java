package site.leesoyeon.avalanche.api.filters;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class ResponseFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);
    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            String correlationId = filterUtils.getCorrelationId(exchange.getRequest().getHeaders());
            logger.debug("상위 헤더에 상관관계 ID를 추가합니다. {}", correlationId);
            exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
            logger.debug("{}에 대한 나가는 요청을 완료합니다.", exchange.getRequest().getURI());
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}