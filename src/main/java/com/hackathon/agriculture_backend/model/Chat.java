package com.hackathon.agriculture_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @JsonIgnore
    private Farmer farmer;
    
    // Getter for farmerId to match frontend expectations
    public Long getFarmerId() {
        return farmer != null ? farmer.getId() : null;
    }
    
    @Column(name = "user_message", nullable = false, columnDefinition = "TEXT")
    private String userMessage;
    
    @Column(name = "ai_response", nullable = false, columnDefinition = "TEXT")
    private String aiResponse;
    
    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData; // JSON string containing farmer's records for context
    
    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.GENERAL;
    
    @Column(name = "is_helpful")
    private Boolean isHelpful;
    
    @Column(name = "user_feedback")
    private String userFeedback;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "created_at")
    private Instant createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    public enum MessageType {
        GENERAL,
        IRRIGATION_ADVICE,
        CROP_MANAGEMENT,
        WEATHER_QUERY,
        PEST_DISEASE,
        SOIL_HEALTH,
        FERTILIZER_ADVICE,
        HARVEST_PLANNING,
        MARKET_INFO,
        TECHNICAL_SUPPORT,
        DISEASE_TREATMENT
    }
}
