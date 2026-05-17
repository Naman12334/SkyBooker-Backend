package com.skybooker.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        log.info(">>> REQUEST  | {} {} | From: {}",
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress() != null
                        ? request.getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        );

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("<<< RESPONSE | {} {} | Status: {} | {}ms",
                    request.getMethod(),
                    request.getURI().getPath(),
                    response.getStatusCode(),
                    elapsed
            );
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}