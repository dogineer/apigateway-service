package com.develop.web.Filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            HttpCookie sessionCookie = request.getCookies().getFirst("SESSION");

            if (config.isPreLogger()) {
                log.info("[Gateway Filter] {} Request Id : {} / Path                 -> {} ", sessionCookie, request.getId(), request.getPath());
                if (!request.getQueryParams().isEmpty()) {
                log.info("[Gateway Filter] Request Id : {} / Path                 -> {}", request.getId(), request.getPath());
                }
            }
            request.getQueryParams();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                log.info("[Gateway Filter] Request Id : {} / Response status code -> {}", request.getId(), response.getStatusCode());
                }
            }));
        });
    }
}