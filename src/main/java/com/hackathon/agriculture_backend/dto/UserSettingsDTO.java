package com.hackathon.agriculture_backend.dto;

import lombok.Data;

@Data
public class UserSettingsDTO {
    private Long id;

    // Profile settings
    private String phone;
    private String location;
    private String bio;

    // Notification settings
    private boolean emailAlerts;
    private boolean smsAlerts;
    private boolean pushNotifications;
    private boolean weeklyReports;
    private boolean systemUpdates;

    // Appearance settings
    private String theme;
    private String language;
    private String timezone;
    private String dateFormat;

    // Privacy settings
    private String profileVisibility;
    private boolean dataSharing;
    private boolean analytics;
    private boolean marketing;

    // Security settings
    private boolean twoFactorAuth;
    private int sessionTimeout;
    private boolean loginNotifications;
}
