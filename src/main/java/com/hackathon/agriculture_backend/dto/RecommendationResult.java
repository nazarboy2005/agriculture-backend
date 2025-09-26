package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResult {
    
    private String recommendation;
    private String explanation;
    private Double waterSavedLiters;
    private Integer score;
    
    public RecommendationResult(String recommendation, String explanation) {
        this.recommendation = recommendation;
        this.explanation = explanation;
        this.waterSavedLiters = 0.0;
        this.score = 0;
    }
}



