package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.AdminMetricsDto;
import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.WaterSavingsDto;
import com.hackathon.agriculture_backend.service.FarmerService;
import com.hackathon.agriculture_backend.repository.IrrigationRecommendationRepository;
import com.hackathon.agriculture_backend.repository.AlertLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://127.0.0.1:3000",
    "https://agriculture-frontend-two.vercel.app",
    "https://agriculture-frontend.vercel.app"
}, allowCredentials = "true")
public class AdminController {
    
    private final FarmerService farmerService;
    private final IrrigationRecommendationRepository recommendationRepository;
    private final AlertLogRepository alertLogRepository;
    
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminMetricsDto>> getAdminMetrics() {
        log.info("Fetching admin metrics");
        
        try {
            // Create mock admin metrics for now
            AdminMetricsDto metrics = new AdminMetricsDto();
            metrics.setTotalFarmers(25L);
            metrics.setActiveFarmers(23L);
            metrics.setFarmersWithSmsOptIn(20L);
            metrics.setTotalWaterSavedLiters(1500.5);
            metrics.setTotalRecommendations(150L);
            metrics.setSuccessfulAlerts(45L);
            metrics.setFailedAlerts(2L);
            
            // Mock crop distribution
            List<AdminMetricsDto.CropDistribution> cropDistribution = List.of(
                new AdminMetricsDto.CropDistribution("Wheat", 10L, 40.0),
                new AdminMetricsDto.CropDistribution("Rice", 8L, 32.0),
                new AdminMetricsDto.CropDistribution("Corn", 7L, 28.0)
            );
            metrics.setCropDistribution(cropDistribution);
            
            // Mock recommendation distribution
            List<AdminMetricsDto.RecommendationDistribution> recommendationDistribution = List.of(
                new AdminMetricsDto.RecommendationDistribution("HIGH", 45L, 30.0),
                new AdminMetricsDto.RecommendationDistribution("MEDIUM", 60L, 40.0),
                new AdminMetricsDto.RecommendationDistribution("LOW", 45L, 30.0)
            );
            metrics.setRecommendationDistribution(recommendationDistribution);
            
            // Mock alert type distribution
            List<AdminMetricsDto.AlertTypeDistribution> alertTypeDistribution = List.of(
                new AdminMetricsDto.AlertTypeDistribution("IRRIGATION", 30L, 60.0),
                new AdminMetricsDto.AlertTypeDistribution("WEATHER", 15L, 30.0),
                new AdminMetricsDto.AlertTypeDistribution("CROP", 5L, 10.0)
            );
            metrics.setAlertTypeDistribution(alertTypeDistribution);
            
            // Mock daily metrics
            Map<String, Object> dailyMetrics = Map.of(
                "todayRecommendations", 12,
                "todayWaterSaved", 85.5,
                "todayAlerts", 3,
                "activeUsers", 18
            );
            metrics.setDailyMetrics(dailyMetrics);
            
            return ResponseEntity.ok(ApiResponse.success("Admin metrics retrieved successfully", metrics));
            
        } catch (Exception e) {
            log.error("Error fetching admin metrics", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch admin metrics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/farmers/{farmerId}/water-savings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WaterSavingsDto>> getWaterSavings(
            @PathVariable Long farmerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        log.info("Fetching water savings for farmer ID: {} from {} to {}", farmerId, from, to);
        
        try {
            // Create mock water savings data
            WaterSavingsDto waterSavings = new WaterSavingsDto();
            waterSavings.setFarmerId(farmerId);
            waterSavings.setFarmerName("John Doe"); // Mock farmer name
            waterSavings.setFromDate(from);
            waterSavings.setToDate(to);
            waterSavings.setTotalWaterSavedLiters(250.5);
            
            // Create mock daily savings data
            List<WaterSavingsDto.DailyWaterSavings> dailySavings = List.of(
                new WaterSavingsDto.DailyWaterSavings(
                    from.plusDays(0), "Wheat", "HIGH", 45.2, "High irrigation saved 45.2L water"
                ),
                new WaterSavingsDto.DailyWaterSavings(
                    from.plusDays(1), "Wheat", "MEDIUM", 30.1, "Medium irrigation saved 30.1L water"
                ),
                new WaterSavingsDto.DailyWaterSavings(
                    from.plusDays(2), "Wheat", "LOW", 15.8, "Low irrigation saved 15.8L water"
                )
            );
            waterSavings.setDailySavings(dailySavings);
            
            return ResponseEntity.ok(ApiResponse.success("Water savings retrieved successfully", waterSavings));
            
        } catch (Exception e) {
            log.error("Error fetching water savings", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch water savings: " + e.getMessage()));
        }
    }
    
    @GetMapping("/data/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> exportData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "json") String format) {
        
        log.info("Exporting data from {} to {} in {} format", from, to, format);
        
        try {
            // Mock data export response
            String exportData = String.format(
                "Data export for period %s to %s in %s format\n" +
                "Export includes:\n" +
                "- Farmer data\n" +
                "- Irrigation recommendations\n" +
                "- Alert logs\n" +
                "- Water savings data\n" +
                "Export file: agriculture_data_%s_to_%s.%s",
                from, to, format, from, to, format
            );
            
            return ResponseEntity.ok(ApiResponse.success("Data export completed successfully", exportData));
            
        } catch (Exception e) {
            log.error("Error exporting data", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to export data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/farmers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalFarmers() {
        log.info("Fetching total farmers count");
        
        try {
            Long count = farmerService.getTotalFarmersCount();
            return ResponseEntity.ok(ApiResponse.success(count));
            
        } catch (Exception e) {
            log.error("Error fetching total farmers count", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch farmers count: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/sms-opt-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getFarmersWithSmsOptIn() {
        log.info("Fetching farmers with SMS opt-in count");
        
        try {
            Long count = farmerService.getFarmersWithSmsOptInCount();
            return ResponseEntity.ok(ApiResponse.success(count));
            
        } catch (Exception e) {
            log.error("Error fetching SMS opt-in count", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch SMS opt-in count: " + e.getMessage()));
        }
    }
}
