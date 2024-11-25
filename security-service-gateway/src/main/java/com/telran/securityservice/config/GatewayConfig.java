package com.telran.securityservice.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class GatewayConfig {

    @Bean
    public GatewayFilter addRequestHeaderFilter() {
        return (exchange, chain) -> {
            String requestId = UUID.randomUUID().toString();
            return chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-Request-Id", requestId)
                            .build())
                    .build());
        };
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authentication", r -> r.path("/auth/**")
                        .uri("http://authentication-service:8080"))
                .route("branches", r -> r.path("/branches/**")
                        .uri("http://branche-service:8081"))
                .route("contacts", r -> r.path("/contacts/**")
                        .uri("http://contact-service:8082"))
                .route("courses", r -> r.path("/courses/**")
                        .uri("http://course-service:8083"))
                .route("groups", r -> r.path("/groups/**")
                        .or().path("/lecturers_by_groups/**")
                        .or().path("/weekdays/**")
                        .uri("http://group-service:8084"))
                .route("lecturers", r -> r.path("/lecturers/**")
                        .uri("http://lecturer-service:8086"))
                .route("logs", r -> r.path("/logs/**")
                        .uri("http://log-service:8087"))
                .route("notifications", r -> r.path("/notifications/**")
                        .uri("http://notification-service:8090"))
                .route("payments", r -> r.path("/payments/**")
                        .or().path("/payment_types/**")
                        .uri("http://payment-service:8091"))
                .route("statuses", r -> r.path("/statuses/**")
                        .uri("http://status-service:8092"))
                .route("students", r -> r.path("/students/**")
                        .uri("http://student-service:8093"))
                .build();
    }
}
