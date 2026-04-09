package com.eventhub.gateway.filter;

import com.eventhub.gateway.dto.AuthenticationDetails;
import com.eventhub.gateway.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();


            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7).trim();


            if (jwtUtils.isTokenInvalid(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            try {

                String userId = jwtUtils.getUserId(token);
                AuthenticationDetails authDetails=jwtUtils.getAuthenticationDetails(token);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", authDetails.getUserId())
                        .header("X-User-Name",authDetails.getName())
                        .header("X-Email",authDetails.getEmail())
                        .header("X-User-Roles",authDetails.getRoles())
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}