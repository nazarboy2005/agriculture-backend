package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSavingsDto {
    
    private Long farmerId;
    private String farmerName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Double totalWaterSavedLiters;
    private List<DailyWaterSavings> dailySavings;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyWaterSavings {
        private LocalDate date;
        private String cropType;
        private String recommendation;
        private Double waterSavedLiters;
        private String explanation;
    }
}


