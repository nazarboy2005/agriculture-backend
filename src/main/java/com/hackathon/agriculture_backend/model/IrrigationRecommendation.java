package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "irrigation_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @NotNull(message = "Farmer is required")
    private Farmer farmer;
    
    @NotNull(message = "Date is required")
    @Column(name = "recommendation_date")
    private LocalDate date;
    
    @NotBlank(message = "Crop type is required")
    @Size(max = 50, message = "Crop type must not exceed 50 characters")
    @Column(name = "crop_type")
    private String cropType;
    
    @NotBlank(message = "Location name is required")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    @Column(name = "location_name")
    private String locationName;
    
    @NotNull(message = "Temperature is required")
    @Column(name = "temperature_celsius")
    private Double tempC;
    
    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Humidity must be between 0 and 100")
    private Double humidity;
    
    @NotNull(message = "Rainfall is required")
    @DecimalMin(value = "0.0", message = "Rainfall must be positive")
    @Column(name = "rainfall_mm")
    private Double rainfallMm;
    
    @NotNull(message = "Evapotranspiration is required")
    @DecimalMin(value = "0.0", message = "Evapotranspiration must be positive")
    @Column(name = "evapotranspiration")
    private Double evapotranspiration;
    
    @NotBlank(message = "Recommendation is required")
    @Pattern(regexp = "^(LOW|MODERATE|HIGH)$", message = "Recommendation must be LOW, MODERATE, or HIGH")
    private String recommendation;
    
    @NotBlank(message = "Explanation is required")
    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    private String explanation;
    
    @Column(name = "water_saved_liters")
    private Double waterSavedLiters;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
