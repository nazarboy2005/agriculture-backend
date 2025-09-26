package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.DiseaseDetectionResponse;
import com.hackathon.agriculture_backend.dto.DetailedDiseaseDetectionResponse;
import com.hackathon.agriculture_backend.model.DiseaseDetectionHistory;
import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.service.PlantDiseaseService;
import com.hackathon.agriculture_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/disease")
@RequiredArgsConstructor
@Slf4j
public class PlantDiseaseController {

    private final PlantDiseaseService plantDiseaseService;
    private final UserService userService;

    @PostMapping("/detect")
    public ResponseEntity<DiseaseDetectionResponse> detectDisease(
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        try {
            log.info("Received disease detection request for image: {}", image.getOriginalFilename());
            
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Get user ID from authentication - allow demo without auth
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                userId = 1L; // Use demo user ID for unauthenticated requests
                log.info("Using demo user ID for disease detection");
            }

            DetailedDiseaseDetectionResponse detailedResponse = plantDiseaseService.detectDisease(image, userId);
            log.info("Disease detection completed: {}", detailedResponse.getPrimaryDisease());
            
            // Convert to simple response format for frontend
            DiseaseDetectionResponse response = new DiseaseDetectionResponse(
                detailedResponse.getPrimaryDisease(),
                detailedResponse.getPrimaryConfidence(),
                detailedResponse.getPrimarySuggestion(),
                detailedResponse.getIsHealthy(),
                detailedResponse.getHealthProbability(),
                detailedResponse.getIsPlant(),
                detailedResponse.getPlantProbability(),
                detailedResponse.getHealthStatus()
            );
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error processing image for disease detection", e);
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for disease detection", e);
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("API configuration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DiseaseDetectionResponse("Service Unavailable", 0.0, e.getMessage(), 
                    false, 0.0, false, 0.0, "Service Unavailable"));
        } catch (Exception e) {
            log.error("Unexpected error during disease detection", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<DiseaseDetectionHistory>> getDetectionHistory(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<DiseaseDetectionHistory> history = plantDiseaseService.getDetectionHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error retrieving detection history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<Void> deleteDetectionHistory(
            @PathVariable Long historyId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            plantDiseaseService.deleteDetectionHistory(userId, historyId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request to delete detection history: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting detection history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/history")
    public ResponseEntity<Void> deleteAllDetectionHistory(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            plantDiseaseService.deleteAllDetectionHistory(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting all detection history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Disease detection endpoint is working!");
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Plant Disease Detection");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        
        try {
            // The principal contains the email address, not the user ID
            String userEmail = authentication.getName();
            User user = userService.findByEmail(userEmail);
            return user.getId();
        } catch (Exception e) {
            log.error("Error getting user ID from authentication for email: {}", authentication.getName(), e);
            return null;
        }
    }
}
