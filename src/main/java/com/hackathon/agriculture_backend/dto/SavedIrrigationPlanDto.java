package com.hackathon.agriculture_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedIrrigationPlanDto {
    
    private Long id;
    private Long farmerId;
    private String planName;
    private Double locationLat;
    private Double locationLng;
    private String cropType;
    private Double area;
    private String irrigationType;
    private String irrigationRate;
    private String emittersPerM2;
    private String soilType;
    private String waterBudget;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
