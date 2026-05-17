package com.skybooker.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> OPEN_ENDPOINTS = List.of(

            // ── Auth ────────────────────────────────────────────────
            "/auth/register",
            "/auth/login",
            "/auth/logout",
            "/auth/refresh",
            "/auth/validate",

            // ── Airlines & Airports (public read) ───────────────────
            "/airlines",
            "/airports",

            // ── Flights (public search) ─────────────────────────────
            "/flights/search",
            "/flights/search/roundtrip",
            "/flights/available",
            "/flights/number/",
            "/flights/airline/",

            // ── Seats (public view) ─────────────────────────────────
            "/seats/available/",
            "/seats/class/",
            "/seats/map/",
            "/seats/count/",

            // ── Bookings (public PNR lookup) ────────────────────────
            "/bookings/pnr/",
            "/bookings/fare",

            // ── Gateway & Actuator ──────────────────────────────────
            "/gateway/info",
            "/gateway/health",
            "/actuator/health"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_ENDPOINTS
                    .stream()
                    .noneMatch(uri -> request.getURI()
                            .getPath().contains(uri));
}