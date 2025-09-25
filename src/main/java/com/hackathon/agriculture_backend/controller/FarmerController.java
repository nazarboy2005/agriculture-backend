package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.FarmerDto;
import com.hackathon.agriculture_backend.service.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/farmers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class FarmerController {
    
    private final FarmerService farmerService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<FarmerDto>> createFarmer(@Valid @RequestBody FarmerDto farmerDto) {
        log.info("Creating farmer: {}", farmerDto.getName());
        
        try {
            FarmerDto createdFarmer = farmerService.createFarmer(farmerDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Farmer created successfully", createdFarmer));
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating farmer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating farmer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create farmer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmerDto>> getFarmerById(@PathVariable Long id) {
        log.info("Fetching farmer with ID: {}", id);
        
        return farmerService.getFarmerById(id)
                .map(farmer -> ResponseEntity.ok(ApiResponse.success(farmer)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<FarmerDto>> getFarmerByPhone(@PathVariable String phone) {
        log.info("Fetching farmer with phone: {}", phone);
        
        return farmerService.getFarmerByPhone(phone)
                .map(farmer -> ResponseEntity.ok(ApiResponse.success(farmer)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmerDto>>> getAllFarmers() {
        log.info("Fetching all farmers");
        
        List<FarmerDto> farmers = farmerService.getAllFarmers();
        return ResponseEntity.ok(ApiResponse.success(farmers));
    }
    
    @GetMapping("/sms-opt-in")
    public ResponseEntity<ApiResponse<List<FarmerDto>>> getFarmersWithSmsOptIn() {
        log.info("Fetching farmers with SMS opt-in");
        
        List<FarmerDto> farmers = farmerService.getFarmersWithSmsOptIn();
        return ResponseEntity.ok(ApiResponse.success(farmers));
    }
    
    @GetMapping("/location/{locationName}")
    public ResponseEntity<ApiResponse<List<FarmerDto>>> getFarmersByLocation(@PathVariable String locationName) {
        log.info("Fetching farmers by location: {}", locationName);
        
        List<FarmerDto> farmers = farmerService.getFarmersByLocation(locationName);
        return ResponseEntity.ok(ApiResponse.success(farmers));
    }
    
    @GetMapping("/crop/{preferredCrop}")
    public ResponseEntity<ApiResponse<List<FarmerDto>>> getFarmersByCrop(@PathVariable String preferredCrop) {
        log.info("Fetching farmers by crop: {}", preferredCrop);
        
        List<FarmerDto> farmers = farmerService.getFarmersByCrop(preferredCrop);
        return ResponseEntity.ok(ApiResponse.success(farmers));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FarmerDto>> updateFarmer(@PathVariable Long id, 
                                                             @Valid @RequestBody FarmerDto farmerDto) {
        log.info("Updating farmer with ID: {}", id);
        
        try {
            FarmerDto updatedFarmer = farmerService.updateFarmer(id, farmerDto);
            return ResponseEntity.ok(ApiResponse.success("Farmer updated successfully", updatedFarmer));
        } catch (IllegalArgumentException e) {
            log.error("Error updating farmer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error updating farmer: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating farmer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update farmer: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFarmer(@PathVariable Long id) {
        log.info("Deleting farmer with ID: {}", id);
        
        try {
            farmerService.deleteFarmer(id);
            return ResponseEntity.ok(ApiResponse.success("Farmer deleted successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Error deleting farmer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error deleting farmer: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting farmer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete farmer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/total")
    public ResponseEntity<ApiResponse<Long>> getTotalFarmersCount() {
        log.info("Fetching total farmers count");
        
        Long count = farmerService.getTotalFarmersCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @GetMapping("/stats/sms-opt-in")
    public ResponseEntity<ApiResponse<Long>> getFarmersWithSmsOptInCount() {
        log.info("Fetching farmers with SMS opt-in count");
        
        Long count = farmerService.getFarmersWithSmsOptInCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}

