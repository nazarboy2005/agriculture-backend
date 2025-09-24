package com.hackathon.agriculture_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationPlanDto {
    
    private String locationName;
    private String cropType;
    private Double area;
    private String irrigationType;
    private String soilType;
    private List<DayPlan> dailyPlans;
    private LocalDate generatedAt;
    private Double totalWaterSaved;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayPlan {
        private LocalDate date;
        private Double etc;
        private Double liters;
        private Integer minutes;
        private String time;
        private String heatRisk;
        private Double waterSavedLiters;
        private String notes;
    }
}
