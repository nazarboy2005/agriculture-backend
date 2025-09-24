package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.FarmerDto;
import com.hackathon.agriculture_backend.model.Chat;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import com.hackathon.agriculture_backend.repository.ChatRepository;
import com.hackathon.agriculture_backend.repository.IrrigationRecommendationRepository;
import com.hackathon.agriculture_backend.repository.SavedIrrigationPlanRepository;
import com.hackathon.agriculture_backend.repository.AlertLogRepository;
import com.hackathon.agriculture_backend.model.SavedIrrigationPlan;
import com.hackathon.agriculture_backend.model.AlertLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final GeminiService geminiService;
    private final FarmerService farmerService;
    private final RecommendationService recommendationService;
    private final WeatherService weatherService;
    private final IrrigationRecommendationRepository recommendationRepository;
    private final SavedIrrigationPlanRepository savedIrrigationPlanRepository;
    private final AlertLogRepository alertLogRepository;
    
    public CompletableFuture<Chat> sendMessage(Long farmerId, String userMessage, String messageType) {
        log.info("Processing chat message from farmer ID: {}", farmerId);
        
        try {
            // Get farmer information
            Optional<FarmerDto> farmerDtoOpt = farmerService.getFarmerById(farmerId);
            if (farmerDtoOpt.isEmpty()) {
                log.warn("Farmer not found with ID: {}, creating mock farmer", farmerId);
                // Create a mock farmer for demo purposes
                FarmerDto mockFarmer = new FarmerDto();
                mockFarmer.setId(farmerId);
                mockFarmer.setName("Demo Farmer");
                mockFarmer.setLocationName("Demo Location");
                mockFarmer.setPreferredCrop("Tomato");
                farmerDtoOpt = Optional.of(mockFarmer);
            }
            
            // Convert DTO to Entity for internal use
            FarmerDto farmerDto = farmerDtoOpt.get();
            Farmer farmer = new Farmer();
            farmer.setId(farmerDto.getId());
            farmer.setName(farmerDto.getName());
            farmer.setPhone(farmerDto.getPhone());
            farmer.setLocationName(farmerDto.getLocationName());
            farmer.setPreferredCrop(farmerDto.getPreferredCrop());
            farmer.setSmsOptIn(farmerDto.getSmsOptIn());
            
            // Get recent recommendations for context
            List<IrrigationRecommendation> allRecommendations = recommendationRepository.findByFarmerIdOrderByDateDesc(farmerId);
            List<IrrigationRecommendation> recentRecommendations = allRecommendations.stream()
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
            
            // Get saved irrigation plans
            List<SavedIrrigationPlan> savedPlans = savedIrrigationPlanRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId);
            
            // Get recent heat alerts
            List<AlertLog> recentAlerts = alertLogRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());
            
            // Get current weather data
            String weatherData = getCurrentWeatherData(farmer);
            
            // Build comprehensive context data
            String contextData = buildComprehensiveContextData(farmer, recentRecommendations, savedPlans, recentAlerts);
            
            // Generate AI response using Gemini
            return geminiService.generatePersonalizedResponse(
                    farmer, 
                    userMessage, 
                    recentRecommendations, 
                    weatherData, 
                    contextData
            ).thenApply(aiResponse -> {
                try {
                    // Save chat to database
                    Chat chat = new Chat();
                    chat.setFarmer(farmer);
                    chat.setUserMessage(userMessage);
                    chat.setAiResponse(aiResponse);
                    chat.setContextData(contextData);
                    chat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                    
                    return chatRepository.save(chat);
                } catch (Exception e) {
                    log.error("Error saving chat to database: {}", e.getMessage());
                    // Return chat object without saving to database
                    Chat chat = new Chat();
                    chat.setId(1L); // Mock ID
                    chat.setFarmer(farmer);
                    chat.setUserMessage(userMessage);
                    chat.setAiResponse(aiResponse);
                    chat.setContextData(contextData);
                    chat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                    return chat;
                }
            });
            
        } catch (Exception e) {
            log.error("Error processing chat message: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public List<Chat> getChatHistory(Long farmerId) {
        try {
            log.info("Fetching chat history for farmer ID: {}", farmerId);
            return chatRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId);
        } catch (Exception e) {
            log.error("Error fetching chat history for farmer {}: {}", farmerId, e.getMessage());
            // Return empty list instead of throwing exception
            return new ArrayList<>();
        }
    }
    
    public Page<Chat> getChatHistory(Long farmerId, Pageable pageable) {
        return chatRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId, pageable);
    }
    
    public List<Chat> getChatHistoryByType(Long farmerId, Chat.MessageType messageType) {
        return chatRepository.findByFarmerIdAndMessageTypeOrderByCreatedAtDesc(farmerId, messageType);
    }
    
    public List<Chat> searchChatHistory(Long farmerId, String query) {
        return chatRepository.findByFarmerIdAndSearchQuery(farmerId, query);
    }
    
    public List<Chat> getRecentChats(Long farmerId, int limit) {
        return chatRepository.findRecentChatsByFarmerId(farmerId, limit);
    }
    
    public Chat updateFeedback(Long chatId, Boolean isHelpful, String feedback) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        chat.setIsHelpful(isHelpful);
        chat.setUserFeedback(feedback);
        
        return chatRepository.save(chat);
    }
    
    public List<Object[]> getMessageTypeStats(Long farmerId) {
        return chatRepository.getMessageTypeStatsByFarmerId(farmerId);
    }
    
    public Double getAverageHelpfulness(Long farmerId) {
        return chatRepository.getAverageHelpfulnessByFarmerId(farmerId);
    }
    
    public Long getTotalChats(Long farmerId) {
        return chatRepository.countByFarmerId(farmerId);
    }
    
    public Long getChatsByType(Long farmerId, Chat.MessageType messageType) {
        return chatRepository.countByFarmerIdAndMessageType(farmerId, messageType);
    }
    
    private String getCurrentWeatherData(Farmer farmer) {
        try {
            // This would integrate with your weather service
            return String.format(
                    "Current weather at %s: Temperature 28°C, Humidity 65%%, Rainfall 0mm, Wind Speed 5 km/h",
                    farmer.getLocationName()
            );
        } catch (Exception e) {
            log.warn("Could not fetch weather data: {}", e.getMessage());
            return "Weather data unavailable";
        }
    }
    
    private String buildComprehensiveContextData(Farmer farmer, List<IrrigationRecommendation> recommendations, 
                                                 List<SavedIrrigationPlan> savedPlans, List<AlertLog> recentAlerts) {
        StringBuilder context = new StringBuilder();
        
        // Farmer Profile
        context.append("FARMER PROFILE:\n");
        context.append("- Name: ").append(farmer.getName()).append("\n");
        context.append("- Location: ").append(farmer.getLocationName()).append("\n");
        context.append("- Crop: ").append(farmer.getPreferredCrop()).append("\n");
        context.append("- SMS Enabled: ").append(farmer.getSmsOptIn() ? "Yes" : "No").append("\n\n");
        
        // Recent Irrigation Recommendations
        if (recommendations != null && !recommendations.isEmpty()) {
            context.append("RECENT IRRIGATION RECOMMENDATIONS:\n");
            for (IrrigationRecommendation rec : recommendations) {
                context.append("- Date: ").append(rec.getDate()).append("\n");
                context.append("  Recommendation: ").append(rec.getRecommendation()).append("\n");
                context.append("  Temperature: ").append(rec.getTempC()).append("°C\n");
                context.append("  Humidity: ").append(rec.getHumidity()).append("%\n");
                context.append("  Water Saved: ").append(rec.getWaterSavedLiters()).append(" liters\n");
                context.append("  Explanation: ").append(rec.getExplanation()).append("\n\n");
            }
        }
        
        // Saved Irrigation Plans
        if (savedPlans != null && !savedPlans.isEmpty()) {
            context.append("SAVED IRRIGATION PLANS:\n");
            for (SavedIrrigationPlan plan : savedPlans) {
                context.append("- Plan Name: ").append(plan.getPlanName()).append("\n");
                context.append("  Crop Type: ").append(plan.getCropType()).append("\n");
                context.append("  Area: ").append(plan.getArea()).append(" hectares\n");
                context.append("  Irrigation Type: ").append(plan.getIrrigationType()).append("\n");
                context.append("  Soil Type: ").append(plan.getSoilType()).append("\n");
                context.append("  Water Budget: ").append(plan.getWaterBudget()).append("\n");
                context.append("  Is Default: ").append(plan.getIsDefault() ? "Yes" : "No").append("\n");
                context.append("  Created: ").append(plan.getCreatedAt()).append("\n\n");
            }
        }
        
        // Recent Heat Alerts
        if (recentAlerts != null && !recentAlerts.isEmpty()) {
            context.append("RECENT HEAT ALERTS:\n");
            for (AlertLog alert : recentAlerts) {
                context.append("- Type: ").append(alert.getType()).append("\n");
                context.append("  Status: ").append(alert.getStatus()).append("\n");
                context.append("  Message: ").append(alert.getMessage()).append("\n");
                context.append("  Created: ").append(alert.getCreatedAt()).append("\n");
                if (alert.getSentAt() != null) {
                    context.append("  Sent At: ").append(alert.getSentAt()).append("\n");
                }
                context.append("\n");
            }
        }
        
        return context.toString();
    }
    
    private String buildContextData(Farmer farmer, List<IrrigationRecommendation> recommendations) {
        StringBuilder context = new StringBuilder();
        
        context.append("Farmer Profile:\n");
        context.append("- Location: ").append(farmer.getLocationName()).append("\n");
        context.append("- Crop: ").append(farmer.getPreferredCrop()).append("\n");
        context.append("- SMS Enabled: ").append(farmer.getSmsOptIn()).append("\n\n");
        
        if (recommendations != null && !recommendations.isEmpty()) {
            context.append("Recent Irrigation History:\n");
            for (IrrigationRecommendation rec : recommendations) {
                context.append("- ").append(rec.getDate()).append(": ").append(rec.getRecommendation())
                       .append(" (").append(rec.getExplanation()).append(")\n");
            }
        }
        
        return context.toString();
    }
}
