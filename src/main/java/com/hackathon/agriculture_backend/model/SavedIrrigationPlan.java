package com.hackathon.agriculture_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_irrigation_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedIrrigationPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;
    
    @Column(name = "plan_name", nullable = false)
    private String planName;
    
    @Column(name = "location_lat", nullable = false)
    private Double locationLat;
    
    @Column(name = "location_lng", nullable = false)
    private Double locationLng;
    
    @Column(name = "crop_type", nullable = false)
    private String cropType;
    
    @Column(name = "area", nullable = false)
    private Double area;
    
    @Column(name = "irrigation_type", nullable = false)
    private String irrigationType;
    
    @Column(name = "irrigation_rate")
    private String irrigationRate;
    
    @Column(name = "emitters_per_m2")
    private String emittersPerM2;
    
    @Column(name = "soil_type", nullable = false)
    private String soilType;
    
    @Column(name = "water_budget")
    private String waterBudget;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
