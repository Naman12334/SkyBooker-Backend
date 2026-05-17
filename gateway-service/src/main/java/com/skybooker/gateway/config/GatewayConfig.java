package com.skybooker.gateway.config;

import com.skybooker.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public RouteLocator skyBookerRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // ── 1. AUTH-SERVICE (8081) ───────────────────────────
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8081")
                )

                // ── 2. AIRLINE-SERVICE /airlines (8082) ──────────────
                .route("airline-service-airlines", r -> r
                        .path("/airlines/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8082")
                )

                // ── 3. AIRLINE-SERVICE /airports (8082) ──────────────
                .route("airline-service-airports", r -> r
                        .path("/airports/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8082")
                )

                // ── 4. FLIGHT-SERVICE (8083) ─────────────────────────
                .route("flight-service", r -> r
                        .path("/flights/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8083")
                )

                // ── 5. SEAT-SERVICE (8084) ───────────────────────────
                .route("seat-service", r -> r
                        .path("/seats/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8084")
                )

                // ── 6. PASSENGER-SERVICE (8085) ──────────────────────
                .route("passenger-service", r -> r
                        .path("/passengers/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8085")
                )

                // ── 7. BOOKING-SERVICE (8086) ────────────────────────
                .route("booking-service", r -> r
                        .path("/bookings/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8086")
                )

                // ── 8. NOTIFICATION-SERVICE (8088) ───────────────────
                .route("notification-service", r -> r
                        .path("/notifications/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter.apply(
                                        new JwtAuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By",
                                        "SkyBooker-Gateway")
                        )
                        .uri("http://localhost:8088")
                )

                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}