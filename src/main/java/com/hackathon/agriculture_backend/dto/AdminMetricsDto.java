package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMetricsDto {
    
    private Long totalFarmers;
    private Long activeFarmers;
    private Long farmersWithSmsOptIn;
    private Double totalWaterSavedLiters;
    private Long totalRecommendations;
    private Long successfulAlerts;
    private Long failedAlerts;
    private List<CropDistribution> cropDistribution;
    private List<RecommendationDistribution> recommendationDistribution;
    private List<AlertTypeDistribution> alertTypeDistribution;
    private Map<String, Object> dailyMetrics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CropDistribution {
        private String cropName;
        private Long farmerCount;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationDistribution {
        private String recommendation;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertTypeDistribution {
        private String alertType;
        private Long count;
        private Double percentage;
    }
}
