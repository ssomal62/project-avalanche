package site.leesoyeon.avalanche.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UserContextWebFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserContextWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        UserContext context = new UserContext();
        context.setCorrelationId(headers.getFirst(UserContext.CORRELATION_ID));
        context.setUserId(headers.getFirst(UserContext.USER_ID));
        context.setAuthToken(headers.getFirst(UserContext.AUTH_TOKEN));
        context.setClientId(headers.getFirst(UserContext.CLIENT_ID));

        UserContextHolder.setContext(context);

        logger.debug("UserContextWebFilter Correlation id: {}", context.getCorrelationId());

        return chain.filter(exchange).contextWrite(ctx -> ctx.put(UserContext.class, context));
    }
}