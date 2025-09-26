package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.UserSettingsDTO;

public interface UserSettingsService {
    
    UserSettingsDTO getUserSettings(Long userId);
    
    UserSettingsDTO saveUserSettings(Long userId, UserSettingsDTO settingsDTO);
    
    void deleteUserSettings(Long userId);
}