package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.FarmerZoneDto;
import com.hackathon.agriculture_backend.service.FarmerZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/farmer-zones")
@RequiredArgsConstructor
@Slf4j
public class FarmerZoneController {
    
    private final FarmerZoneService farmerZoneService;
    
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<List<FarmerZoneDto>>> getZonesByFarmerId(@PathVariable Long farmerId) {
        log.info("Getting zones for farmer ID: {}", farmerId);
        try {
            List<FarmerZoneDto> zones = farmerZoneService.getZonesByFarmerId(farmerId);
            return ResponseEntity.ok(ApiResponse.success("Zones retrieved successfully", zones));
        } catch (Exception e) {
            log.error("Error getting zones for farmer ID {}: {}", farmerId, e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to retrieve zones: " + e.getMessage()));
        }
    }
    
    @PostMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<FarmerZoneDto>> createZone(
            @PathVariable Long farmerId,
            @RequestBody FarmerZoneDto zoneDto) {
        log.info("Creating zone for farmer ID: {} with name: {}", farmerId, zoneDto.getName());
        try {
            FarmerZoneDto createdZone = farmerZoneService.createZone(farmerId, zoneDto);
            return ResponseEntity.ok(ApiResponse.success("Zone created successfully", createdZone));
        } catch (Exception e) {
            log.error("Error creating zone for farmer ID {}: {}", farmerId, e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to create zone: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{zoneId}/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<FarmerZoneDto>> updateZone(
            @PathVariable Long zoneId,
            @PathVariable Long farmerId,
            @RequestBody FarmerZoneDto zoneDto) {
        log.info("Updating zone ID: {} for farmer ID: {}", zoneId, farmerId);
        try {
            FarmerZoneDto updatedZone = farmerZoneService.updateZone(farmerId, zoneId, zoneDto);
            return ResponseEntity.ok(ApiResponse.success("Zone updated successfully", updatedZone));
        } catch (Exception e) {
            log.error("Error updating zone ID {} for farmer ID {}: {}", zoneId, farmerId, e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to update zone: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{zoneId}/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(
            @PathVariable Long zoneId,
            @PathVariable Long farmerId) {
        log.info("Deleting zone ID: {} for farmer ID: {}", zoneId, farmerId);
        try {
            farmerZoneService.deleteZone(farmerId, zoneId);
            return ResponseEntity.ok(ApiResponse.success("Zone deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting zone ID {} for farmer ID {}: {}", zoneId, farmerId, e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to delete zone: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{zoneId}/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<FarmerZoneDto>> getZoneById(
            @PathVariable Long zoneId,
            @PathVariable Long farmerId) {
        log.info("Getting zone ID: {} for farmer ID: {}", zoneId, farmerId);
        try {
            FarmerZoneDto zone = farmerZoneService.getZoneById(farmerId, zoneId);
            return ResponseEntity.ok(ApiResponse.success("Zone retrieved successfully", zone));
        } catch (Exception e) {
            log.error("Error getting zone ID {} for farmer ID {}: {}", zoneId, farmerId, e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to retrieve zone: " + e.getMessage()));
        }
    }
}

