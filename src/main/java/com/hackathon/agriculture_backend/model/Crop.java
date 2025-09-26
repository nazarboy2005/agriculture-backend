package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Crop name is required")
    @Size(min = 2, max = 50, message = "Crop name must be between 2 and 50 characters")
    @Column(unique = true)
    private String name;
    
    @NotNull(message = "Water demand is required")
    @DecimalMin(value = "0.0", message = "Water demand must be positive")
    @Column(name = "water_demand_liters_per_hectare")
    private Double waterDemandLitersPerHectare;
    
    @NotNull(message = "ET threshold is required")
    @DecimalMin(value = "0.0", message = "ET threshold must be positive")
    @Column(name = "et_threshold")
    private Double etThreshold;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}



