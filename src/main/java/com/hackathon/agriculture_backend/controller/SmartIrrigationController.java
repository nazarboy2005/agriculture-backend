package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.dto.IrrigationPlanDto;
import com.hackathon.agriculture_backend.dto.SavedIrrigationPlanDto;
import com.hackathon.agriculture_backend.service.SmartIrrigationService;
import com.hackathon.agriculture_backend.service.SavedIrrigationPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/smart-irrigation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://127.0.0.1:3000",
    "https://agriculture-frontend-two.vercel.app",
    "https://agriculture-frontend.vercel.app"
}, allowCredentials = "true")
public class SmartIrrigationController {
    
    private final SmartIrrigationService smartIrrigationService;
    private final SavedIrrigationPlanService savedIrrigationPlanService;
    
    @PostMapping("/generate-plan")
    public ResponseEntity<ApiResponse<IrrigationPlanDto>> generateIrrigationPlan(
            @RequestBody IrrigationPlanRequest request) {
        
        log.info("Generating irrigation plan for location: {}, {} with crop: {}", 
                request.getLatitude(), request.getLongitude(), request.getCropType());
        
        try {
            IrrigationPlanDto plan = smartIrrigationService.generateIrrigationPlan(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getCropType(),
                    request.getArea(),
                    request.getIrrigationType(),
                    request.getSoilType()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Irrigation plan generated successfully", plan));
            
        } catch (Exception e) {
            log.error("Error generating irrigation plan: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to generate irrigation plan: " + e.getMessage()));
        }
    }
    
    @GetMapping("/heat-alerts/{latitude}/{longitude}")
    public ResponseEntity<ApiResponse<List<HeatAlertDto>>> getHeatAlerts(
            @PathVariable Double latitude,
            @PathVariable Double longitude) {
        
        log.info("Fetching heat alerts for location: {}, {}", latitude, longitude);
        
        try {
            List<HeatAlertDto> alerts = smartIrrigationService.getHeatAlerts(latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("Heat alerts retrieved successfully", alerts));
            
        } catch (Exception e) {
            log.error("Error fetching heat alerts: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch heat alerts: " + e.getMessage()));
        }
    }
    
    @GetMapping("/weather-data/{latitude}/{longitude}")
    public ResponseEntity<ApiResponse<WeatherDataDto>> getWeatherData(
            @PathVariable Double latitude,
            @PathVariable Double longitude) {
        
        log.info("Fetching weather data for location: {}, {}", latitude, longitude);
        
        try {
            WeatherDataDto weatherData = smartIrrigationService.getWeatherData(latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("Weather data retrieved successfully", weatherData));
            
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch weather data: " + e.getMessage()));
        }
    }
    
    @PostMapping("/save-plan")
    public ResponseEntity<ApiResponse<SavedIrrigationPlanDto>> savePlan(
            @RequestBody SavePlanRequest request) {
        
        log.info("Saving irrigation plan for farmer: {} with name: {}", 
                request.getFarmerId(), request.getPlanName());
        
        try {
            SavedIrrigationPlanDto planDto = new SavedIrrigationPlanDto();
            planDto.setFarmerId(request.getFarmerId());
            planDto.setPlanName(request.getPlanName());
            planDto.setLocationLat(request.getLocationLat());
            planDto.setLocationLng(request.getLocationLng());
            planDto.setCropType(request.getCropType());
            planDto.setArea(request.getArea());
            planDto.setIrrigationType(request.getIrrigationType());
            planDto.setIrrigationRate(request.getIrrigationRate());
            planDto.setEmittersPerM2(request.getEmittersPerM2());
            planDto.setSoilType(request.getSoilType());
            planDto.setWaterBudget(request.getWaterBudget());
            planDto.setIsDefault(request.getIsDefault());
            
            SavedIrrigationPlanDto savedPlan = savedIrrigationPlanService.savePlan(request.getFarmerId(), planDto);
            return ResponseEntity.ok(ApiResponse.success("Plan saved successfully", savedPlan));
            
        } catch (Exception e) {
            log.error("Error saving irrigation plan: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to save plan: " + e.getMessage()));
        }
    }
    
    @GetMapping("/saved-plans/{farmerId}")
    public ResponseEntity<ApiResponse<List<SavedIrrigationPlanDto>>> getSavedPlans(
            @PathVariable Long farmerId) {
        
        log.info("Fetching saved plans for farmer: {}", farmerId);
        
        try {
            List<SavedIrrigationPlanDto> plans = savedIrrigationPlanService.getPlansByFarmerId(farmerId);
            return ResponseEntity.ok(ApiResponse.success("Plans retrieved successfully", plans));
            
        } catch (Exception e) {
            log.error("Error fetching saved plans: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch plans: " + e.getMessage()));
        }
    }
    
    @GetMapping("/saved-plans/{farmerId}/{planId}")
    public ResponseEntity<ApiResponse<SavedIrrigationPlanDto>> getSavedPlan(
            @PathVariable Long farmerId,
            @PathVariable Long planId) {
        
        log.info("Fetching saved plan: {} for farmer: {}", planId, farmerId);
        
        try {
            SavedIrrigationPlanDto plan = savedIrrigationPlanService.getPlanById(farmerId, planId);
            return ResponseEntity.ok(ApiResponse.success("Plan retrieved successfully", plan));
            
        } catch (Exception e) {
            log.error("Error fetching saved plan: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to fetch plan: " + e.getMessage()));
        }
    }
    
    @PutMapping("/saved-plans/{farmerId}/{planId}")
    public ResponseEntity<ApiResponse<SavedIrrigationPlanDto>> updateSavedPlan(
            @PathVariable Long farmerId,
            @PathVariable Long planId,
            @RequestBody SavePlanRequest request) {
        
        log.info("Updating saved plan: {} for farmer: {}", planId, farmerId);
        
        try {
            SavedIrrigationPlanDto planDto = new SavedIrrigationPlanDto();
            planDto.setPlanName(request.getPlanName());
            planDto.setLocationLat(request.getLocationLat());
            planDto.setLocationLng(request.getLocationLng());
            planDto.setCropType(request.getCropType());
            planDto.setArea(request.getArea());
            planDto.setIrrigationType(request.getIrrigationType());
            planDto.setIrrigationRate(request.getIrrigationRate());
            planDto.setEmittersPerM2(request.getEmittersPerM2());
            planDto.setSoilType(request.getSoilType());
            planDto.setWaterBudget(request.getWaterBudget());
            planDto.setIsDefault(request.getIsDefault());
            
            SavedIrrigationPlanDto updatedPlan = savedIrrigationPlanService.updatePlan(farmerId, planId, planDto);
            return ResponseEntity.ok(ApiResponse.success("Plan updated successfully", updatedPlan));
            
        } catch (Exception e) {
            log.error("Error updating saved plan: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update plan: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/saved-plans/{farmerId}/{planId}")
    public ResponseEntity<ApiResponse<String>> deleteSavedPlan(
            @PathVariable Long farmerId,
            @PathVariable Long planId) {
        
        log.info("Deleting saved plan: {} for farmer: {}", planId, farmerId);
        
        try {
            savedIrrigationPlanService.deletePlan(farmerId, planId);
            return ResponseEntity.ok(ApiResponse.success("Plan deleted successfully", "Plan deleted"));
            
        } catch (Exception e) {
            log.error("Error deleting saved plan: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to delete plan: " + e.getMessage()));
        }
    }
    
    @PostMapping("/saved-plans/{farmerId}/{planId}/set-default")
    public ResponseEntity<ApiResponse<SavedIrrigationPlanDto>> setAsDefault(
            @PathVariable Long farmerId,
            @PathVariable Long planId) {
        
        log.info("Setting plan: {} as default for farmer: {}", planId, farmerId);
        
        try {
            SavedIrrigationPlanDto plan = savedIrrigationPlanService.setAsDefault(farmerId, planId);
            return ResponseEntity.ok(ApiResponse.success("Plan set as default successfully", plan));
            
        } catch (Exception e) {
            log.error("Error setting plan as default: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to set plan as default: " + e.getMessage()));
        }
    }
    
    // Request DTOs
    public static class IrrigationPlanRequest {
        private Double latitude;
        private Double longitude;
        private String cropType;
        private Double area;
        private String irrigationType;
        private String soilType;
        
        // Getters and setters
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        
        public String getCropType() { return cropType; }
        public void setCropType(String cropType) { this.cropType = cropType; }
        
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        
        public String getIrrigationType() { return irrigationType; }
        public void setIrrigationType(String irrigationType) { this.irrigationType = irrigationType; }
        
        public String getSoilType() { return soilType; }
        public void setSoilType(String soilType) { this.soilType = soilType; }
    }
    
    public static class HeatAlertDto {
        private String date;
        private String time;
        private Double temperature;
        private Double heatIndex;
        private String riskLevel;
        private String recommendations;
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        
        public Double getHeatIndex() { return heatIndex; }
        public void setHeatIndex(Double heatIndex) { this.heatIndex = heatIndex; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        
        public String getRecommendations() { return recommendations; }
        public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    }
    
    public static class WeatherDataDto {
        private Double temperature;
        private Double humidity;
        private Double rainfall;
        private Double windSpeed;
        private Double uvIndex;
        private String heatRisk;
        
        // Getters and setters
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        
        public Double getHumidity() { return humidity; }
        public void setHumidity(Double humidity) { this.humidity = humidity; }
        
        public Double getRainfall() { return rainfall; }
        public void setRainfall(Double rainfall) { this.rainfall = rainfall; }
        
        public Double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
        
        public Double getUvIndex() { return uvIndex; }
        public void setUvIndex(Double uvIndex) { this.uvIndex = uvIndex; }
        
        public String getHeatRisk() { return heatRisk; }
        public void setHeatRisk(String heatRisk) { this.heatRisk = heatRisk; }
    }
    
    public static class SavePlanRequest {
        private Long farmerId;
        private String planName;
        private Double locationLat;
        private Double locationLng;
        private String cropType;
        private Double area;
        private String irrigationType;
        private String irrigationRate;
        private String emittersPerM2;
        private String soilType;
        private String waterBudget;
        private Boolean isDefault;
        
        // Getters and setters
        public Long getFarmerId() { return farmerId; }
        public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }
        
        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }
        
        public Double getLocationLat() { return locationLat; }
        public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
        
        public Double getLocationLng() { return locationLng; }
        public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
        
        public String getCropType() { return cropType; }
        public void setCropType(String cropType) { this.cropType = cropType; }
        
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        
        public String getIrrigationType() { return irrigationType; }
        public void setIrrigationType(String irrigationType) { this.irrigationType = irrigationType; }
        
        public String getIrrigationRate() { return irrigationRate; }
        public void setIrrigationRate(String irrigationRate) { this.irrigationRate = irrigationRate; }
        
        public String getEmittersPerM2() { return emittersPerM2; }
        public void setEmittersPerM2(String emittersPerM2) { this.emittersPerM2 = emittersPerM2; }
        
        public String getSoilType() { return soilType; }
        public void setSoilType(String soilType) { this.soilType = soilType; }
        
        public String getWaterBudget() { return waterBudget; }
        public void setWaterBudget(String waterBudget) { this.waterBudget = waterBudget; }
        
        public Boolean getIsDefault() { return isDefault; }
        public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    }
}
