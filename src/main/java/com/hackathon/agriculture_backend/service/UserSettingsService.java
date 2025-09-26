package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.UserSettingsDto;
import com.hackathon.agriculture_backend.model.UserSettings;
import com.hackathon.agriculture_backend.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingsService {
    
    private final UserSettingsRepository userSettingsRepository;
    
    public UserSettingsDto getUserSettings(Long userId) {
        log.info("Fetching user settings for user ID: {}", userId);
        
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        
        if (settings.isPresent()) {
            return convertToDto(settings.get());
        } else {
            // Return default settings if none exist
            log.info("No settings found for user ID: {}, returning defaults", userId);
            return new UserSettingsDto();
        }
    }
    
    @Transactional
    public UserSettingsDto saveUserSettings(Long userId, UserSettingsDto settingsDto) {
        log.info("Saving user settings for user ID: {}", userId);
        
        Optional<UserSettings> existingSettings = userSettingsRepository.findByUserId(userId);
        
        UserSettings settings;
        if (existingSettings.isPresent()) {
            settings = existingSettings.get();
            updateSettingsFromDto(settings, settingsDto);
        } else {
            settings = new UserSettings();
            settings.setUserId(userId);
            updateSettingsFromDto(settings, settingsDto);
        }
        
        UserSettings savedSettings = userSettingsRepository.save(settings);
        log.info("Successfully saved user settings for user ID: {}", userId);
        
        return convertToDto(savedSettings);
    }
    
    @Transactional
    public void deleteUserSettings(Long userId) {
        log.info("Deleting user settings for user ID: {}", userId);
        
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            userSettingsRepository.delete(settings.get());
            log.info("Successfully deleted user settings for user ID: {}", userId);
        } else {
            log.warn("No settings found to delete for user ID: {}", userId);
        }
    }
    
    private UserSettingsDto convertToDto(UserSettings settings) {
        UserSettingsDto dto = new UserSettingsDto();
        
        // Profile settings
        dto.setPhone(settings.getPhone());
        dto.setLocation(settings.getLocation());
        dto.setBio(settings.getBio());
        
        // Notification settings
        dto.setEmailAlerts(settings.getEmailAlerts());
        dto.setSmsAlerts(settings.getSmsAlerts());
        dto.setPushNotifications(settings.getPushNotifications());
        dto.setWeeklyReports(settings.getWeeklyReports());
        dto.setSystemUpdates(settings.getSystemUpdates());
        
        // Appearance settings
        dto.setTheme(settings.getTheme());
        dto.setLanguage(settings.getLanguage());
        dto.setTimezone(settings.getTimezone());
        dto.setDateFormat(settings.getDateFormat());
        
        // Privacy settings
        dto.setProfileVisibility(settings.getProfileVisibility());
        dto.setDataSharing(settings.getDataSharing());
        dto.setAnalytics(settings.getAnalytics());
        dto.setMarketing(settings.getMarketing());
        
        // Security settings
        dto.setTwoFactorAuth(settings.getTwoFactorAuth());
        dto.setSessionTimeout(settings.getSessionTimeout());
        dto.setLoginNotifications(settings.getLoginNotifications());
        
        return dto;
    }
    
    private void updateSettingsFromDto(UserSettings settings, UserSettingsDto dto) {
        // Profile settings
        settings.setPhone(dto.getPhone());
        settings.setLocation(dto.getLocation());
        settings.setBio(dto.getBio());
        
        // Notification settings
        settings.setEmailAlerts(dto.getEmailAlerts());
        settings.setSmsAlerts(dto.getSmsAlerts());
        settings.setPushNotifications(dto.getPushNotifications());
        settings.setWeeklyReports(dto.getWeeklyReports());
        settings.setSystemUpdates(dto.getSystemUpdates());
        
        // Appearance settings
        settings.setTheme(dto.getTheme());
        settings.setLanguage(dto.getLanguage());
        settings.setTimezone(dto.getTimezone());
        settings.setDateFormat(dto.getDateFormat());
        
        // Privacy settings
        settings.setProfileVisibility(dto.getProfileVisibility());
        settings.setDataSharing(dto.getDataSharing());
        settings.setAnalytics(dto.getAnalytics());
        settings.setMarketing(dto.getMarketing());
        
        // Security settings
        settings.setTwoFactorAuth(dto.getTwoFactorAuth());
        settings.setSessionTimeout(dto.getSessionTimeout());
        settings.setLoginNotifications(dto.getLoginNotifications());
    }
}