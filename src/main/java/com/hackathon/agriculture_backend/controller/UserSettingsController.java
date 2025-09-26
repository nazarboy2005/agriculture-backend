package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.UserSettingsDto;
import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.service.UserService;
import com.hackathon.agriculture_backend.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/settings")
@RequiredArgsConstructor
@Slf4j
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<UserSettingsDto>> getUserSettings(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            UserSettingsDto settings = userSettingsService.getUserSettings(userId);
            return ResponseEntity.ok(ApiResponse.success("User settings retrieved successfully", settings));
        } catch (Exception e) {
            log.error("Error retrieving user settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to retrieve user settings: " + e.getMessage()));
        }
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<UserSettingsDto>> saveUserSettings(
            @RequestBody UserSettingsDto settingsDto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            UserSettingsDto savedSettings = userSettingsService.saveUserSettings(userId, settingsDto);
            return ResponseEntity.ok(ApiResponse.success("User settings saved successfully", savedSettings));
        } catch (Exception e) {
            log.error("Error saving user settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to save user settings: " + e.getMessage()));
        }
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUserSettings(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            userSettingsService.deleteUserSettings(userId);
            return ResponseEntity.ok(ApiResponse.success("User settings deleted successfully", "Settings reset to defaults"));
        } catch (Exception e) {
            log.error("Error deleting user settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to delete user settings: " + e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserSettingsDto>> getProfileSettings(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            UserSettingsDto settings = userSettingsService.getUserSettings(userId);
            // Return only profile-related settings
            UserSettingsDto profileSettings = new UserSettingsDto();
            profileSettings.setPhone(settings.getPhone());
            profileSettings.setLocation(settings.getLocation());
            profileSettings.setBio(settings.getBio());
            
            return ResponseEntity.ok(ApiResponse.success("Profile settings retrieved successfully", profileSettings));
        } catch (Exception e) {
            log.error("Error retrieving profile settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to retrieve profile settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserSettingsDto>> saveProfileSettings(
            @RequestBody UserSettingsDto profileDto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            // Get existing settings and update only profile fields
            UserSettingsDto existingSettings = userSettingsService.getUserSettings(userId);
            existingSettings.setPhone(profileDto.getPhone());
            existingSettings.setLocation(profileDto.getLocation());
            existingSettings.setBio(profileDto.getBio());
            
            UserSettingsDto savedSettings = userSettingsService.saveUserSettings(userId, existingSettings);
            return ResponseEntity.ok(ApiResponse.success("Profile settings saved successfully", savedSettings));
        } catch (Exception e) {
            log.error("Error saving profile settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to save profile settings: " + e.getMessage()));
        }
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<UserSettingsDto>> getNotificationSettings(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            UserSettingsDto settings = userSettingsService.getUserSettings(userId);
            // Return only notification-related settings
            UserSettingsDto notificationSettings = new UserSettingsDto();
            notificationSettings.setEmailAlerts(settings.getEmailAlerts());
            notificationSettings.setSmsAlerts(settings.getSmsAlerts());
            notificationSettings.setPushNotifications(settings.getPushNotifications());
            notificationSettings.setWeeklyReports(settings.getWeeklyReports());
            notificationSettings.setSystemUpdates(settings.getSystemUpdates());
            
            return ResponseEntity.ok(ApiResponse.success("Notification settings retrieved successfully", notificationSettings));
        } catch (Exception e) {
            log.error("Error retrieving notification settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to retrieve notification settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/notifications")
    public ResponseEntity<ApiResponse<UserSettingsDto>> saveNotificationSettings(
            @RequestBody UserSettingsDto notificationDto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("User not authenticated"));
            }
            
            // Get existing settings and update only notification fields
            UserSettingsDto existingSettings = userSettingsService.getUserSettings(userId);
            existingSettings.setEmailAlerts(notificationDto.getEmailAlerts());
            existingSettings.setSmsAlerts(notificationDto.getSmsAlerts());
            existingSettings.setPushNotifications(notificationDto.getPushNotifications());
            existingSettings.setWeeklyReports(notificationDto.getWeeklyReports());
            existingSettings.setSystemUpdates(notificationDto.getSystemUpdates());
            
            UserSettingsDto savedSettings = userSettingsService.saveUserSettings(userId, existingSettings);
            return ResponseEntity.ok(ApiResponse.success("Notification settings saved successfully", savedSettings));
        } catch (Exception e) {
            log.error("Error saving notification settings", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Failed to save notification settings: " + e.getMessage()));
        }
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        
        try {
            String userEmail = authentication.getName();
            User user = userService.findByEmail(userEmail);
            return user.getId();
        } catch (Exception e) {
            log.error("Error getting user ID from authentication for email: {}", authentication.getName(), e);
            return null;
        }
    }
}