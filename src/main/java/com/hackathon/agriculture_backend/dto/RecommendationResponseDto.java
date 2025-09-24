package com.hackathon.agriculture_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    
    private Long id;
    private String zoneName;
    private LocalDate date;
    private String cropType;
    private String category;
    private String recommendation;
    private Integer waterSaved;
    private String riskLevel; // low, medium, high
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private Double etc;
    private Boolean isAIGenerated;
    private String weatherDescription;
    private String additionalNotes;
}
