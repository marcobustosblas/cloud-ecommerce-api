package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    // Captura el momento exacto en que se inició la aplicación
    private static final LocalDateTime START_TIME = LocalDateTime.now();

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {

        // se usa LinkedHashMap para que los datos salgan ordenados en el JSON
        Map<String, Object> healthReport = new LinkedHashMap<>();
        healthReport.put("status", "UP");
        healthReport.put("timestamp", LocalDateTime.now());
        healthReport.put("startedAt", START_TIME);

        return ResponseEntity.ok(healthReport);
    }

}

// el orden de linked hashmap respetará el orden estricto de mis líneas de código
