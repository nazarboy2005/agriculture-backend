package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.model.AlertLog;
import com.hackathon.agriculture_backend.service.AlertService;
import com.hackathon.agriculture_backend.service.FarmerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/alerts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AlertController {
    
    private final AlertService alertService;
    private final FarmerService farmerService;
    
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> sendTestAlert(@RequestParam Long farmerId) {
        log.info("Sending test alert for farmer ID: {}", farmerId);
        
        try {
            // Get farmer
            var farmerOpt = farmerService.getFarmerById(farmerId);
            if (farmerOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Farmer not found with ID: " + farmerId));
            }
            
            // Convert DTO to entity for service
            var farmerDto = farmerOpt.get();
            var farmer = new com.hackathon.agriculture_backend.model.Farmer();
            farmer.setId(farmerDto.getId());
            farmer.setName(farmerDto.getName());
            farmer.setPhone(farmerDto.getPhone());
            farmer.setLocationName(farmerDto.getLocationName());
            farmer.setSmsOptIn(farmerDto.getSmsOptIn());
            
            // Send test alert
            AlertLog alertLog = alertService.sendTestAlert(farmer);
            
            String message = "Test alert sent successfully. Status: " + alertLog.getStatus();
            if (alertLog.getStatus().equals("FAILED")) {
                message += ". Error: " + alertLog.getErrorDetails();
            }
            
            return ResponseEntity.ok(ApiResponse.success(message));
            
        } catch (Exception e) {
            log.error("Error sending test alert", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send test alert: " + e.getMessage()));
        }
    }
    
    @GetMapping("/farmers/{farmerId}")
    public ResponseEntity<ApiResponse<List<AlertLog>>> getFarmerAlerts(@PathVariable Long farmerId) {
        log.info("Fetching alerts for farmer ID: {}", farmerId);
        
        try {
            List<AlertLog> alerts = alertService.getAlertsByFarmer(farmerId);
            return ResponseEntity.ok(ApiResponse.success(alerts));
            
        } catch (Exception e) {
            log.error("Error fetching farmer alerts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch alerts: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AlertLog>>> getAlertsByStatus(@PathVariable String status) {
        log.info("Fetching alerts by status: {}", status);
        
        try {
            List<AlertLog> alerts = alertService.getAlertsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(alerts));
            
        } catch (Exception e) {
            log.error("Error fetching alerts by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch alerts: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<AlertLog>>> getAlertsByType(@PathVariable String type) {
        log.info("Fetching alerts by type: {}", type);
        
        try {
            List<AlertLog> alerts = alertService.getAlertsByType(type);
            return ResponseEntity.ok(ApiResponse.success(alerts));
            
        } catch (Exception e) {
            log.error("Error fetching alerts by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch alerts: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/successful")
    public ResponseEntity<ApiResponse<Long>> getSuccessfulAlertsCount(
            @RequestParam Long farmerId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        log.info("Fetching successful alerts count for farmer ID: {}", farmerId);
        
        try {
            // Mock successful alerts count
            Long successfulCount = 15L; // Mock data
            return ResponseEntity.ok(ApiResponse.success("Successful alerts count retrieved", successfulCount));
            
        } catch (Exception e) {
            log.error("Error fetching successful alerts count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch alerts count: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/failed")
    public ResponseEntity<ApiResponse<Long>> getFailedAlertsCount(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        log.info("Fetching failed alerts count");
        
        try {
            // Mock failed alerts count
            Long failedCount = 3L; // Mock data
            return ResponseEntity.ok(ApiResponse.success("Failed alerts count retrieved", failedCount));
            
        } catch (Exception e) {
            log.error("Error fetching failed alerts count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch failed alerts count: " + e.getMessage()));
        }
    }
}
