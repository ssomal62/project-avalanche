package site.leesoyeon.avalanche.api.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.leesoyeon.avalanche.api.security.JwtTokenProvider;
import site.leesoyeon.avalanche.api.util.UserContext;
import site.leesoyeon.avalanche.api.util.UserContextHolder;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = filterUtils.getCorrelationId(exchange.getRequest().getHeaders());

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/api/v1/product/") || path.startsWith("/api/v1/user/all")) {
            return chain.filter(exchange);
        }

        String accessToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        if (accessToken == null || !jwtTokenProvider.validateAccessToken(accessToken)) {
            String refreshToken = exchange.getRequest().getHeaders().getFirst("Refresh-Token");
            if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                return reissueTokenAndContinue(exchange, chain, refreshToken, correlationId);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        UserContext userContext = new UserContext();
        userContext.setCorrelationId(correlationId);
        userContext.setAuthToken(accessToken);
        userContext.setUserId(jwtTokenProvider.getUserIdFromToken(accessToken));

        String finalAccessToken = accessToken;
        String clientId = jwtTokenProvider.getClientIdFromToken(accessToken);

        return chain.filter(exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add(UserContext.USER_ID, UserContextHolder.getContext().getUserId());
                    headers.add(UserContext.CORRELATION_ID, correlationId);
                    headers.add(UserContext.CLIENT_ID, clientId);
                    headers.add(UserContext.AUTH_TOKEN, finalAccessToken);
                })).build());
    }

    private Mono<Void> reissueTokenAndContinue(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken, String correlationId) {
        // Implement token reissue logic
        return Mono.empty(); // Placeholder
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}