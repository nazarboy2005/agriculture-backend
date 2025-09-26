package com.hackathon.agriculture_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Health check endpoint called");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Agriculture Backend API");
        health.put("version", "1.0.0");
        
        // Check database connectivity
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                health.put("database", "UP");
                health.put("databaseUrl", connection.getMetaData().getURL());
            } else {
                health.put("database", "DOWN");
                health.put("status", "DOWN");
            }
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
            health.put("status", "DOWN");
        }
        
        // Check if we can determine the status
        String status = (String) health.get("status");
        int httpStatus = "UP".equals(status) ? 200 : 503;
        
        return ResponseEntity.status(httpStatus).body(health);
    }

    @GetMapping("/simple")
    public ResponseEntity<String> simpleHealthCheck() {
        log.info("Simple health check endpoint called");
        return ResponseEntity.ok("OK");
    }
}
