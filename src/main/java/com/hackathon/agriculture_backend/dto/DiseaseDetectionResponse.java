package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDetectionResponse {
    private String diseaseName;
    private Double confidence;
    private String suggestion;
    private Boolean isHealthy;
    private Double healthProbability;
    private Boolean isPlant;
    private Double plantProbability;
    private String healthStatus;
}