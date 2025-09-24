package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.RecommendationRequestDto;
import com.hackathon.agriculture_backend.dto.RecommendationResponseDto;
import com.hackathon.agriculture_backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/recommendations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<RecommendationResponseDto>>> generateRecommendations(
            @RequestBody RecommendationRequestDto request) {
        
        log.info("Generating recommendations for farmer ID: {}, zone: {}, category: {}", 
                request.getFarmerId(), request.getZoneId(), request.getCategory());
        
        try {
            List<RecommendationResponseDto> recommendations = recommendationService.generateAIRecommendations(request);
            return ResponseEntity.ok(ApiResponse.success("Recommendations generated successfully", recommendations));
        } catch (Exception e) {
            log.error("Error generating recommendations: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to generate recommendations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponseDto>>> getFarmerRecommendations(
            @PathVariable Long farmerId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String zoneId) {
        
        log.info("Fetching recommendations for farmer ID: {}, category: {}, zone: {}", 
                farmerId, category, zoneId);
        
        try {
            List<RecommendationResponseDto> recommendations = recommendationService.getFarmerRecommendations(
                    farmerId, category, zoneId);
            return ResponseEntity.ok(ApiResponse.success(recommendations));
        } catch (Exception e) {
            log.error("Error fetching farmer recommendations: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch recommendations: " + e.getMessage()));
        }
    }
    
    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponseDto>>> getZoneRecommendations(
            @PathVariable Long zoneId,
            @RequestParam(required = false) String category) {
        
        log.info("Fetching recommendations for zone ID: {}, category: {}", zoneId, category);
        
        try {
            List<RecommendationResponseDto> recommendations = recommendationService.getZoneRecommendations(
                    zoneId, category);
            return ResponseEntity.ok(ApiResponse.success(recommendations));
        } catch (Exception e) {
            log.error("Error fetching zone recommendations: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch zone recommendations: " + e.getMessage()));
        }
    }
}