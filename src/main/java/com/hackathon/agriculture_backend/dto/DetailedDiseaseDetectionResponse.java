package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedDiseaseDetectionResponse {
    private String primaryDisease;
    private Double primaryConfidence;
    private String primarySuggestion;
    private List<DiseaseSuggestion> allDiseases;
    private Boolean isHealthy;
    private Double healthProbability;
    private String healthStatus;
    private Boolean isPlant;
    private Double plantProbability;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiseaseSuggestion {
        private String name;
        private Double probability;
        private String description;
        private String treatment;
        private String prevention;
        private String cause;
        private List<String> commonNames;
        private List<String> classification;
    }
}
