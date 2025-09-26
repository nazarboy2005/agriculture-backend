package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.UserSettingsDTO;
import com.hackathon.agriculture_backend.entity.UserSettings;

public interface UserSettingsService {
    UserSettingsDTO getUserSettings(Long userId);
    UserSettingsDTO updateUserSettings(Long userId, UserSettingsDTO settingsDTO);
}
