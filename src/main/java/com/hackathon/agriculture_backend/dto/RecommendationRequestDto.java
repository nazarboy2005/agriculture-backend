package com.hackathon.agriculture_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    
    private Long farmerId;
    private Long zoneId;
    private String zoneName;
    private Double latitude;
    private Double longitude;
    private String cropType;
    private String category; // irrigation, heat, general
    private LocalDate startDate;
    private LocalDate endDate;
    private String messageType; // For AI context
}
