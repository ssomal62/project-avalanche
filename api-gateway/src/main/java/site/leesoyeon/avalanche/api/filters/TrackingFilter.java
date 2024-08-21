package site.leesoyeon.avalanche.api.filters;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@RequiredArgsConstructor
public class TrackingFilter implements GlobalFilter, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);
	private final FilterUtils filterUtils;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (!isCorrelationIdPresent(exchange)) {
			String correlationID = generateCorrelationId();
			exchange = filterUtils.setCorrelationId(exchange, correlationID);
			logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID);
		} else {
			logger.debug("tmx-correlation-id found in tracking filter: {}. ",
					filterUtils.getCorrelationId(exchange.getRequest().getHeaders()));
		}
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private boolean isCorrelationIdPresent(ServerWebExchange exchange) {
		return filterUtils.getCorrelationId(exchange.getRequest().getHeaders()) != null;
	}

	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}
}