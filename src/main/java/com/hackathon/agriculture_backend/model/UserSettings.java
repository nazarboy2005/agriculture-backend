package com.hackathon.agriculture_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    // Profile settings
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    // Notification settings
    @Column(name = "email_alerts", nullable = false)
    private Boolean emailAlerts = true;
    
    @Column(name = "sms_alerts", nullable = false)
    private Boolean smsAlerts = false;
    
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = true;
    
    @Column(name = "weekly_reports", nullable = false)
    private Boolean weeklyReports = true;
    
    @Column(name = "system_updates", nullable = false)
    private Boolean systemUpdates = false;
    
    // Appearance settings
    @Column(name = "theme", nullable = false)
    private String theme = "light";
    
    @Column(name = "language", nullable = false)
    private String language = "en";
    
    @Column(name = "timezone", nullable = false)
    private String timezone = "UTC";
    
    @Column(name = "date_format", nullable = false)
    private String dateFormat = "MM/DD/YYYY";
    
    // Privacy settings
    @Column(name = "profile_visibility", nullable = false)
    private String profileVisibility = "public";
    
    @Column(name = "data_sharing", nullable = false)
    private Boolean dataSharing = false;
    
    @Column(name = "analytics", nullable = false)
    private Boolean analytics = true;
    
    @Column(name = "marketing", nullable = false)
    private Boolean marketing = false;
    
    // Security settings
    @Column(name = "two_factor_auth", nullable = false)
    private Boolean twoFactorAuth = false;
    
    @Column(name = "session_timeout", nullable = false)
    private Integer sessionTimeout = 30;
    
    @Column(name = "login_notifications", nullable = false)
    private Boolean loginNotifications = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
