package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "alert_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @NotNull(message = "Farmer is required")
    private Farmer farmer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id")
    private IrrigationRecommendation recommendation;
    
    @NotBlank(message = "Alert type is required")
    @Size(max = 50, message = "Alert type must not exceed 50 characters")
    @Column(name = "alert_type")
    private String type;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SENT|FAILED|PENDING)$", message = "Status must be SENT, FAILED, or PENDING")
    private String status;
    
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
    
    @Size(max = 500, message = "Error details must not exceed 500 characters")
    @Column(name = "error_details")
    private String errorDetails;
    
    @Column(name = "sent_at")
    private Instant sentAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}

