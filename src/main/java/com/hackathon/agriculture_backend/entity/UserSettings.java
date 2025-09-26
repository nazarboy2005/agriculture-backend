package com.hackathon.agriculture_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_settings")
@Data
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Profile settings
    private String phone;
    private String location;
    private String bio;

    // Notification settings
    private boolean emailAlerts = true;
    private boolean smsAlerts = false;
    private boolean pushNotifications = true;
    private boolean weeklyReports = true;
    private boolean systemUpdates = false;

    // Appearance settings
    private String theme = "light";
    private String language = "en";
    private String timezone = "UTC";
    private String dateFormat = "MM/DD/YYYY";

    // Privacy settings
    private String profileVisibility = "public";
    private boolean dataSharing = false;
    private boolean analytics = true;
    private boolean marketing = false;

    // Security settings
    private boolean twoFactorAuth = false;
    private int sessionTimeout = 30;
    private boolean loginNotifications = true;
}
