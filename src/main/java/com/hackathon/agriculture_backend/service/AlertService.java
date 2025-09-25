package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.model.AlertLog;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import com.hackathon.agriculture_backend.repository.AlertLogRepository;
// Twilio imports commented out - will be added later
// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertService {
    
    private final AlertLogRepository alertLogRepository;
    
    // Twilio configuration commented out - will be added later
    // @Value("${app.twilio.account.sid}")
    // private String accountSid;
    
    // @Value("${app.twilio.auth.token}")
    // private String authToken;
    
    // @Value("${app.twilio.phone.number}")
    // private String fromPhoneNumber;
    
    public AlertLog sendSmsAlert(Farmer farmer, String message, String alertType) {
        log.info("Sending SMS alert to farmer: {} ({})", farmer.getName(), farmer.getPhone());
        
        try {
            // Mock SMS sending - Twilio will be added later
            log.info("MOCK SMS SENT to {}: {}", farmer.getPhone(), message);
            
            // Simulate SMS sending delay
            Thread.sleep(100);
            
            // Log successful alert
            AlertLog alertLog = new AlertLog();
            alertLog.setFarmer(farmer);
            alertLog.setType(alertType);
            alertLog.setStatus("SENT");
            alertLog.setMessage(message);
            alertLog.setSentAt(Instant.now());
            
            AlertLog savedAlert = alertLogRepository.save(alertLog);
            log.info("Mock SMS alert sent successfully. Message ID: MOCK-{}", System.currentTimeMillis());
            
            return savedAlert;
            
        } catch (Exception e) {
            log.error("Failed to send mock SMS alert to farmer: {} - {}", farmer.getPhone(), e.getMessage());
            
            // Log failed alert
            AlertLog alertLog = new AlertLog();
            alertLog.setFarmer(farmer);
            alertLog.setType(alertType);
            alertLog.setStatus("FAILED");
            alertLog.setMessage(message);
            alertLog.setErrorDetails("Mock SMS failed: " + e.getMessage());
            alertLog.setSentAt(Instant.now());
            
            return alertLogRepository.save(alertLog);
        }
    }
    
    public AlertLog sendIrrigationRecommendationAlert(Farmer farmer, IrrigationRecommendation recommendation) {
        log.info("Sending irrigation recommendation alert to farmer: {}", farmer.getName());
        
        String message = buildIrrigationMessage(farmer, recommendation);
        return sendSmsAlert(farmer, message, "IRRIGATION_RECOMMENDATION");
    }
    
    public AlertLog sendHeatAlert(Farmer farmer, Double temperature) {
        log.info("Sending heat alert to farmer: {} for temperature: {}°C", farmer.getName(), temperature);
        
        String message = String.format(
                "🌡️ HEAT ALERT for %s!\n" +
                "Temperature: %.1f°C\n" +
                "Please take extra precautions:\n" +
                "• Increase irrigation frequency\n" +
                "• Provide shade for crops\n" +
                "• Monitor soil moisture closely\n" +
                "• Consider early morning/evening irrigation",
                farmer.getLocationName(), temperature
        );
        
        return sendSmsAlert(farmer, message, "HEAT_ALERT");
    }
    
    public AlertLog sendTestAlert(Farmer farmer) {
        log.info("Sending test alert to farmer: {}", farmer.getName());
        
        String message = String.format(
                "🧪 Test Alert from Smart Irrigation System\n" +
                "Hello %s!\n" +
                "This is a test message to verify SMS functionality.\n" +
                "Your phone number: %s\n" +
                "Location: %s\n" +
                "Time: %s",
                farmer.getName(),
                farmer.getPhone(),
                farmer.getLocationName(),
                Instant.now().toString()
        );
        
        return sendSmsAlert(farmer, message, "TEST");
    }
    
    public List<AlertLog> getAlertsByFarmer(Long farmerId) {
        return alertLogRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId);
    }
    
    public List<AlertLog> getAlertsByStatus(String status) {
        return alertLogRepository.findByStatus(status);
    }
    
    public List<AlertLog> getAlertsByType(String type) {
        return alertLogRepository.findByType(type);
    }
    
    public Long countSuccessfulAlerts(Long farmerId, Instant startTime, Instant endTime) {
        return alertLogRepository.countSuccessfulAlertsByFarmerAndDateRange(farmerId, startTime, endTime);
    }
    
    public Long countFailedAlerts(Instant startTime, Instant endTime) {
        return alertLogRepository.countByStatusAndDateRange("FAILED", startTime, endTime);
    }
    
    private String buildIrrigationMessage(Farmer farmer, IrrigationRecommendation recommendation) {
        String emoji = getRecommendationEmoji(recommendation.getRecommendation());
        String urgency = getUrgencyText(recommendation.getRecommendation());
        
        return String.format(
                "🌱 %s Irrigation Recommendation for %s\n" +
                "Date: %s\n" +
                "Crop: %s\n" +
                "Recommendation: %s %s\n\n" +
                "Weather Conditions:\n" +
                "• Temperature: %.1f°C\n" +
                "• Humidity: %.0f%%\n" +
                "• Rainfall: %.1fmm\n" +
                "• Evapotranspiration: %.2fmm\n\n" +
                "Explanation: %s\n\n" +
                "Water Saved: %.1f liters\n" +
                "Stay informed with Smart Irrigation!",
                emoji, farmer.getLocationName(),
                recommendation.getDate(),
                recommendation.getCropType(),
                recommendation.getRecommendation(), urgency,
                recommendation.getTempC(),
                recommendation.getHumidity(),
                recommendation.getRainfallMm(),
                recommendation.getEvapotranspiration(),
                recommendation.getExplanation(),
                recommendation.getWaterSavedLiters()
        );
    }
    
    private String getRecommendationEmoji(String recommendation) {
        switch (recommendation) {
            case "HIGH":
                return "🔴";
            case "MODERATE":
                return "🟡";
            case "LOW":
                return "🟢";
            default:
                return "ℹ️";
        }
    }
    
    private String getUrgencyText(String recommendation) {
        switch (recommendation) {
            case "HIGH":
                return "(URGENT - Water Now!)";
            case "MODERATE":
                return "(Recommended)";
            case "LOW":
                return "(Optional)";
            default:
                return "";
        }
    }
}

