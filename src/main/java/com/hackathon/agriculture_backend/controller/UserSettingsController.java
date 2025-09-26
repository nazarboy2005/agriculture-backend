package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.UserSettingsDTO;
import com.hackathon.agriculture_backend.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<UserSettingsDTO> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        // Assuming userId is stored in UserDetails or can be fetched from a service
        Long userId = getUserIdFromUserDetails(userDetails);
        UserSettingsDTO settings = userSettingsService.getUserSettings(userId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserSettingsDTO settingsDTO) {
        Long userId = getUserIdFromUserDetails(userDetails);
        UserSettingsDTO updatedSettings = userSettingsService.updateUserSettings(userId, settingsDTO);
        return ResponseEntity.ok(updatedSettings);
    }

    // Helper method to extract userId from UserDetails
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // Implement logic to extract userId from UserDetails or fetch from database
        // This is a placeholder implementation
        return 1L; // Replace with actual userId retrieval logic
    }
}
