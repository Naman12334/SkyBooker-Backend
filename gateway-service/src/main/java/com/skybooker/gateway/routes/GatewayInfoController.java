package com.skybooker.gateway.routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayInfoController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> gatewayInfo() {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("gateway",     "SkyBooker API Gateway");
        response.put("version",     "1.0.0");
        response.put("status",      "UP");
        response.put("timestamp",   LocalDateTime.now().toString());
        response.put("gatewayPort", 8080);

        List<Map<String, String>> routes = List.of(
                route("auth-service",      "http://localhost:8081", "/auth/**"),
                route("airline-service",   "http://localhost:8082", "/airlines/**, /airports/**"),
                route("flight-service",    "http://localhost:8083", "/flights/**"),
                route("seat-service",      "http://localhost:8084", "/seats/**"),
                route("passenger-service", "http://localhost:8085", "/passengers/**"),
                route("booking-service",   "http://localhost:8086", "/bookings/**"),
                route("skybooker-web",     "http://localhost:8089", "/web/**")
        );

        response.put("routes", routes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status",    "UP",
                "service",   "SkyBooker API Gateway",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    private Map<String, String> route(String name, String target, String pathPattern) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name",        name);
        m.put("pathPattern", pathPattern);
        m.put("target",      target);
        return m;
    }
}