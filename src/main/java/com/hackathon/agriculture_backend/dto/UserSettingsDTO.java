package com.hackathon.agriculture_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDTO {
    
    // Profile settings
    private String phone;
    private String location;
    private String bio;
    
    // Notification settings
    private Boolean emailAlerts = true;
    private Boolean smsAlerts = false;
    private Boolean pushNotifications = true;
    private Boolean weeklyReports = true;
    private Boolean systemUpdates = false;
    
    // Appearance settings
    private String theme = "light";
    private String language = "en";
    private String timezone = "UTC";
    private String dateFormat = "MM/DD/YYYY";
    
    // Privacy settings
    private String profileVisibility = "public";
    private Boolean dataSharing = false;
    private Boolean analytics = true;
    private Boolean marketing = false;
    
    // Security settings
    private Boolean twoFactorAuth = false;
    private Integer sessionTimeout = 30;
    private Boolean loginNotifications = true;
}