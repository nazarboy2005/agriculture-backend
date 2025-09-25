package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "disease_detection_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDetectionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "image_filename")
    private String imageFilename;
    
    @Column(name = "disease_name")
    private String diseaseName;
    
    @Column(name = "confidence")
    private Double confidence;
    
    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;
    
    @Column(name = "is_healthy")
    private Boolean isHealthy;
    
    @Column(name = "health_probability")
    private Double healthProbability;
    
    @Column(name = "is_plant")
    private Boolean isPlant;
    
    @Column(name = "plant_probability")
    private Double plantProbability;
    
    @Column(name = "health_status")
    private String healthStatus;
    
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(name = "api_key_used")
    private String apiKeyUsed;
    
    @PrePersist
    protected void onCreate() {
        detectedAt = LocalDateTime.now();
    }
}
