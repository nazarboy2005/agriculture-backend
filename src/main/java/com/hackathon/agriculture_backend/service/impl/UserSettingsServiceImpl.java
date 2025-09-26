package com.hackathon.agriculture_backend.service.impl;

import com.hackathon.agriculture_backend.dto.UserSettingsDTO;
import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.model.UserSettings;
import com.hackathon.agriculture_backend.repository.UserRepository;
import com.hackathon.agriculture_backend.repository.UserSettingsRepository;
import com.hackathon.agriculture_backend.service.UserSettingsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserSettingsDTO getUserSettings(Long userId) {
        Optional<UserSettings> optionalSettings = userSettingsRepository.findByUserId(userId);
        UserSettings settings;
        if (optionalSettings.isPresent()) {
            settings = optionalSettings.get();
        } else {
            // Create default settings if not exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            settings = new UserSettings();
            settings.setUserId(userId);
            settings = userSettingsRepository.save(settings);
        }
        UserSettingsDTO dto = new UserSettingsDTO();
        BeanUtils.copyProperties(settings, dto);
        return dto;
    }

    @Override
    public UserSettingsDTO saveUserSettings(Long userId, UserSettingsDTO settingsDTO) {
        Optional<UserSettings> optionalSettings = userSettingsRepository.findByUserId(userId);
        UserSettings settings;
        if (optionalSettings.isPresent()) {
            settings = optionalSettings.get();
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            settings = new UserSettings();
            settings.setUserId(userId);
        }
        BeanUtils.copyProperties(settingsDTO, settings, "id");
        settings = userSettingsRepository.save(settings);
        UserSettingsDTO resultDTO = new UserSettingsDTO();
        BeanUtils.copyProperties(settings, resultDTO);
        return resultDTO;
    }
    
    @Override
    public void deleteUserSettings(Long userId) {
        Optional<UserSettings> optionalSettings = userSettingsRepository.findByUserId(userId);
        if (optionalSettings.isPresent()) {
            userSettingsRepository.delete(optionalSettings.get());
        }
    }
}
