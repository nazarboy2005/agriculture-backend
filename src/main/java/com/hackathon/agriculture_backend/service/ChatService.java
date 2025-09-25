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
        
        // Content filtering - check if message is plant/agriculture related
        if (!isAgricultureRelated(userMessage)) {
            log.info("Non-agriculture related message detected, returning filtered response");
            return CompletableFuture.completedFuture(createFilteredResponse(farmerId, userMessage, messageType));
        }
        
        try {
            // Get farmer information
            Optional<FarmerDto> farmerDtoOpt = farmerService.getFarmerById(farmerId);
            if (farmerDtoOpt.isEmpty()) {
                log.error("Farmer not found with ID: {}, this should not happen if farmer exists", farmerId);
                throw new RuntimeException("Farmer not found with ID: " + farmerId + ". Please ensure the farmer exists in the database.");
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
            // Return a proper error response instead of failing
            Chat errorChat = new Chat();
            errorChat.setId(1L);
            errorChat.setUserMessage(userMessage);
            errorChat.setAiResponse("I apologize, but I'm having trouble processing your request right now. Please try again later.");
            errorChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
            errorChat.setContextData("Error response due to: " + e.getMessage());
            
            // Create a mock farmer for error response
            Farmer mockFarmer = new Farmer();
            mockFarmer.setId(farmerId);
            mockFarmer.setName("Error Response");
            mockFarmer.setLocationName("Unknown");
            mockFarmer.setPreferredCrop("Unknown");
            errorChat.setFarmer(mockFarmer);
            
            return CompletableFuture.completedFuture(errorChat);
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
    
    /**
     * Check if the user message is related to agriculture, farming, or plants
     */
    private boolean isAgricultureRelated(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        
        // Agriculture-related keywords
        String[] agricultureKeywords = {
            "plant", "crop", "farm", "agriculture", "irrigation", "water", "soil", "fertilizer",
            "harvest", "seed", "grow", "cultivate", "yield", "pest", "disease", "weather",
            "temperature", "humidity", "rain", "drought", "flood", "disease", "insect",
            "weed", "nutrient", "nitrogen", "phosphorus", "potassium", "organic", "sustainable",
            "greenhouse", "field", "land", "tractor", "equipment", "machinery", "livestock",
            "cattle", "sheep", "goat", "chicken", "poultry", "dairy", "milk", "egg",
            "vegetable", "fruit", "grain", "wheat", "rice", "corn", "tomato", "potato",
            "onion", "carrot", "lettuce", "cabbage", "spinach", "broccoli", "cauliflower",
            "pepper", "cucumber", "squash", "pumpkin", "bean", "pea", "lentil", "soybean",
            "cotton", "sugarcane", "coffee", "tea", "spice", "herb", "flower", "tree",
            "orchard", "vineyard", "garden", "plot", "acre", "hectare", "yield", "production",
            "market", "price", "profit", "loss", "cost", "investment", "loan", "credit",
            "insurance", "government", "subsidy", "policy", "regulation", "certification",
            "organic", "gmo", "hybrid", "variety", "breed", "strain", "genetics", "breeding",
            "research", "development", "innovation", "technology", "precision", "automation",
            "sensor", "drone", "satellite", "gps", "mapping", "monitoring", "tracking",
            "data", "analytics", "prediction", "forecast", "model", "algorithm", "ai",
            "machine learning", "iot", "smart farming", "digital agriculture", "agtech"
        };
        
        // Check if message contains agriculture-related keywords
        for (String keyword : agricultureKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Generate AI treatment suggestions for plant diseases without farmer context
     */
    public CompletableFuture<Chat> generateDiseaseTreatment(String diseaseName, Double confidence, Boolean isHealthy, String healthStatus) {
        log.info("Generating disease treatment for: {} with confidence: {}", diseaseName, confidence);
        
        try {
            // Create a simple prompt focused only on the disease
            String prompt = String.format(
                "You are a plant disease specialist. Provide treatment recommendations for: %s (%.1f%% confidence, %s health status: %s). " +
                "Focus ONLY on treating this specific disease. Do NOT mention any farmer names, locations, or crop types. " +
                "Provide: 1) Treatment steps, 2) Prevention measures, 3) Recovery timeline, 4) When to seek help. " +
                "Keep response focused on %s only.",
                diseaseName, confidence * 100, isHealthy ? "Healthy" : "Unhealthy", healthStatus, diseaseName
            );
            
            // Create a mock farmer for disease treatment
            Farmer mockFarmer = new Farmer();
            mockFarmer.setId(1L);
            mockFarmer.setName("Disease Treatment Specialist");
            mockFarmer.setLocationName("Unknown");
            mockFarmer.setPreferredCrop("Unknown");
            
            // Call Gemini API with mock farmer context
            String aiResponse = geminiService.generatePersonalizedResponse(
                    mockFarmer, 
                    prompt, 
                    new ArrayList<>(), 
                    "Weather data unavailable", 
                    "Disease treatment context"
            ).join();
            
            // Create a simple chat record
            Chat chat = new Chat();
            chat.setFarmer(mockFarmer); // Set the farmer relationship
            chat.setUserMessage("Disease treatment request for: " + diseaseName);
            chat.setAiResponse(aiResponse);
            chat.setMessageType(Chat.MessageType.PEST_DISEASE);
            chat.setContextData("Disease treatment for: " + diseaseName);
            
            // Save to database
            chat = chatRepository.save(chat);
            
            return CompletableFuture.completedFuture(chat);
            
        } catch (Exception e) {
            log.error("Error generating disease treatment: {}", e.getMessage());
            
            // Create a mock farmer for fallback response
            Farmer fallbackFarmer = new Farmer();
            fallbackFarmer.setId(1L);
            fallbackFarmer.setName("Disease Treatment Specialist");
            fallbackFarmer.setLocationName("Unknown");
            fallbackFarmer.setPreferredCrop("Unknown");
            
            // Return a fallback response
            Chat fallbackChat = new Chat();
            fallbackChat.setFarmer(fallbackFarmer); // Set the farmer relationship
            fallbackChat.setUserMessage("Disease treatment request for: " + diseaseName);
            fallbackChat.setAiResponse("Unable to generate treatment suggestions at the moment. Please try again later.");
            fallbackChat.setMessageType(Chat.MessageType.PEST_DISEASE);
            fallbackChat.setContextData("Disease treatment for: " + diseaseName);
            
            return CompletableFuture.completedFuture(fallbackChat);
        }
    }
    
    /**
     * Create a filtered response for non-agriculture related messages
     */
    private Chat createFilteredResponse(Long farmerId, String userMessage, String messageType) {
        log.info("Creating filtered response for non-agriculture message from farmer ID: {}", farmerId);
        
        try {
            // Create a mock farmer for the response
            Farmer mockFarmer = new Farmer();
            mockFarmer.setId(farmerId);
            mockFarmer.setName("Farmer");
            mockFarmer.setLocationName("Unknown");
            mockFarmer.setPreferredCrop("Unknown");
            
            // Create filtered chat response
            Chat filteredChat = new Chat();
            filteredChat.setFarmer(mockFarmer);
            filteredChat.setUserMessage(userMessage);
            filteredChat.setAiResponse("I'm specialized in agriculture and farming topics. Please ask me about crops, irrigation, plant diseases, weather conditions, or other farming-related questions. How can I help you with your agricultural needs?");
            filteredChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
            filteredChat.setContextData("Filtered response for non-agriculture message");
            
            // Save to database
            return chatRepository.save(filteredChat);
            
        } catch (Exception e) {
            log.error("Error creating filtered response: {}", e.getMessage());
            
            // Return a fallback filtered response
            Chat fallbackChat = new Chat();
            fallbackChat.setId(1L); // Mock ID
            fallbackChat.setUserMessage(userMessage);
            fallbackChat.setAiResponse("I'm specialized in agriculture and farming topics. Please ask me about crops, irrigation, plant diseases, weather conditions, or other farming-related questions. How can I help you with your agricultural needs?");
            fallbackChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
            fallbackChat.setContextData("Filtered response for non-agriculture message");
            
            // Create a mock farmer for fallback response
            Farmer mockFarmer = new Farmer();
            mockFarmer.setId(farmerId);
            mockFarmer.setName("Farmer");
            mockFarmer.setLocationName("Unknown");
            mockFarmer.setPreferredCrop("Unknown");
            fallbackChat.setFarmer(mockFarmer);
            
            return fallbackChat;
        }
    }
}
