package com.hackathon.agriculture_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    
    private Long id;
    
    @NotNull(message = "Farmer ID is required")
    private Long farmerId;
    
    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @NotBlank(message = "Crop type is required")
    @Size(max = 50, message = "Crop type must not exceed 50 characters")
    private String cropType;
    
    @NotBlank(message = "Location name is required")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    private String locationName;
    
    @NotNull(message = "Temperature is required")
    private Double tempC;
    
    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Humidity must be between 0 and 100")
    private Double humidity;
    
    @NotNull(message = "Rainfall is required")
    @DecimalMin(value = "0.0", message = "Rainfall must be positive")
    private Double rainfallMm;
    
    @NotNull(message = "Evapotranspiration is required")
    @DecimalMin(value = "0.0", message = "Evapotranspiration must be positive")
    private Double evapotranspiration;
    
    @NotBlank(message = "Recommendation is required")
    @Pattern(regexp = "^(LOW|MODERATE|HIGH)$", message = "Recommendation must be LOW, MODERATE, or HIGH")
    private String recommendation;
    
    @NotBlank(message = "Explanation is required")
    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    private String explanation;
    
    private Double waterSavedLiters;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
}

