package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.WeatherDto;
import com.hackathon.agriculture_backend.model.AlertLog;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import com.hackathon.agriculture_backend.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduledRecommendationService {
    
    private final FarmerRepository farmerRepository;
    private final FarmerService farmerService;
    private final WeatherService weatherService;
    private final NasaService nasaService;
    private final RecommendationService recommendationService;
    private final AlertService alertService;
    
    @Value("${app.scheduler.enabled:true}")
    private boolean schedulerEnabled;
    
    @Scheduled(cron = "${app.scheduler.daily-recommendation.cron:0 0 6 * * ?}")
    public void sendDailyRecommendations() {
        if (!schedulerEnabled) {
            log.info("Scheduler is disabled, skipping daily recommendations");
            return;
        }
        
        log.info("Starting daily recommendation process at {}", LocalDate.now());
        
        try {
            List<Farmer> farmers = farmerRepository.findBySmsOptInTrue();
            log.info("Processing recommendations for {} farmers", farmers.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (Farmer farmer : farmers) {
                try {
                    processFarmerRecommendation(farmer);
                    successCount++;
                    log.info("Successfully processed recommendation for farmer: {}", farmer.getName());
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to process recommendation for farmer: {} - {}", farmer.getName(), e.getMessage());
                }
            }
            
            log.info("Daily recommendation process completed. Success: {}, Failures: {}", successCount, failureCount);
            
        } catch (Exception e) {
            log.error("Error in daily recommendation process", e);
        }
    }
    
    private void processFarmerRecommendation(Farmer farmer) {
        log.info("Processing recommendation for farmer: {} at location: {}", 
                farmer.getName(), farmer.getLocationName());
        
        LocalDate today = LocalDate.now();
        
        // Check if recommendation already exists for today
        if (recommendationService.hasRecommendationForDate(farmer.getId(), today)) {
            log.info("Recommendation already exists for farmer: {} on date: {}", farmer.getName(), today);
            return;
        }
        
        try {
            // Fetch weather data
            WeatherDto weather = weatherService.getCurrentWeather(farmer.getLatitude(), farmer.getLongitude());
            log.info("Weather data fetched for farmer: {} - Temp: {}°C, Humidity: {}%", 
                    farmer.getName(), weather.getTempC(), weather.getHumidity());
            
            // Fetch ET0 data
            Double et0 = nasaService.getEvapotranspiration(farmer.getLatitude(), farmer.getLongitude(), today);
            log.info("ET0 data fetched for farmer: {} - ET0: {}mm", farmer.getName(), et0);
            
            // Calculate recommendation
            var result = recommendationService.calculateRecommendation(
                    farmer,
                    weather.getTempC(),
                    weather.getHumidity(),
                    weather.getRainfallMm(),
                    weather.getForecastRainfallMm(),
                    et0
            );
            
            log.info("Recommendation calculated for farmer: {} - {}", farmer.getName(), result.getRecommendation());
            
            // Save recommendation
            IrrigationRecommendation recommendation = recommendationService.saveRecommendation(
                    farmer,
                    today,
                    farmer.getPreferredCrop(),
                    farmer.getLocationName(),
                    weather.getTempC(),
                    weather.getHumidity(),
                    weather.getRainfallMm(),
                    et0,
                    result
            );
            
            // Send SMS alert
            AlertLog alertLog = alertService.sendIrrigationRecommendationAlert(farmer, recommendation);
            log.info("SMS alert sent for farmer: {} - Status: {}", farmer.getName(), alertLog.getStatus());
            
            // Send heat alert if temperature is high
            if (weather.getHeatAlert() != null && weather.getHeatAlert()) {
                AlertLog heatAlert = alertService.sendHeatAlert(farmer, weather.getTempC());
                log.info("Heat alert sent for farmer: {} - Status: {}", farmer.getName(), heatAlert.getStatus());
            }
            
        } catch (Exception e) {
            log.error("Error processing recommendation for farmer: {}", farmer.getName(), e);
            throw e;
        }
    }
    
    @Scheduled(cron = "0 0 12 * * ?") // Every day at 12 PM
    public void sendHeatAlerts() {
        if (!schedulerEnabled) {
            log.info("Scheduler is disabled, skipping heat alerts");
            return;
        }
        
        log.info("Starting heat alert process at {}", LocalDate.now());
        
        try {
            List<Farmer> farmers = farmerRepository.findBySmsOptInTrue();
            log.info("Checking heat alerts for {} farmers", farmers.size());
            
            int heatAlertCount = 0;
            
            for (Farmer farmer : farmers) {
                try {
                    WeatherDto weather = weatherService.getCurrentWeather(farmer.getLatitude(), farmer.getLongitude());
                    
                    if (weather.getHeatAlert() != null && weather.getHeatAlert()) {
                        AlertLog heatAlert = alertService.sendHeatAlert(farmer, weather.getTempC());
                        heatAlertCount++;
                        log.info("Heat alert sent for farmer: {} - Status: {}", farmer.getName(), heatAlert.getStatus());
                    }
                    
                } catch (Exception e) {
                    log.error("Error checking heat alert for farmer: {} - {}", farmer.getName(), e.getMessage());
                }
            }
            
            log.info("Heat alert process completed. Alerts sent: {}", heatAlertCount);
            
        } catch (Exception e) {
            log.error("Error in heat alert process", e);
        }
    }
    
    @Scheduled(cron = "0 0 18 * * ?") // Every day at 6 PM
    public void sendEveningReminders() {
        if (!schedulerEnabled) {
            log.info("Scheduler is disabled, skipping evening reminders");
            return;
        }
        
        log.info("Starting evening reminder process at {}", LocalDate.now());
        
        try {
            List<Farmer> farmers = farmerRepository.findBySmsOptInTrue();
            log.info("Sending evening reminders to {} farmers", farmers.size());
            
            int reminderCount = 0;
            
            for (Farmer farmer : farmers) {
                try {
                    // Check if farmer has a recommendation for today
                    if (recommendationService.hasRecommendationForDate(farmer.getId(), LocalDate.now())) {
                        // Send evening reminder
                        String reminderMessage = String.format(
                                "🌅 Evening Reminder for %s\n" +
                                "Don't forget to check your irrigation recommendation for today!\n" +
                                "Visit your dashboard or check your earlier message for details.\n" +
                                "Smart Irrigation System",
                                farmer.getLocationName()
                        );
                        
                        AlertLog reminder = alertService.sendSmsAlert(farmer, reminderMessage, "EVENING_REMINDER");
                        reminderCount++;
                        log.info("Evening reminder sent for farmer: {} - Status: {}", farmer.getName(), reminder.getStatus());
                    }
                    
                } catch (Exception e) {
                    log.error("Error sending evening reminder for farmer: {} - {}", farmer.getName(), e.getMessage());
                }
            }
            
            log.info("Evening reminder process completed. Reminders sent: {}", reminderCount);
            
        } catch (Exception e) {
            log.error("Error in evening reminder process", e);
        }
    }
}


