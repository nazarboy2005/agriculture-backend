package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.model.Chat;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SimpleChatService {
    
    private final ChatRepository chatRepository;
    
    public CompletableFuture<Chat> sendMessage(Long farmerId, String userMessage, String messageType) {
        log.info("Processing simple chat message from farmer ID: {}", farmerId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create a mock farmer
                Farmer farmer = new Farmer();
                farmer.setId(farmerId);
                farmer.setName("Farmer " + farmerId);
                farmer.setLocationName("Farm Location");
                farmer.setPreferredCrop("Mixed Crops");
                
                // Generate a simple AI response
                String aiResponse = generateSimpleResponse(userMessage);
                
                // Create chat object
                Chat chat = new Chat();
                chat.setFarmer(farmer);
                chat.setUserMessage(userMessage);
                chat.setAiResponse(aiResponse);
                chat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                chat.setContextData("Simple chat response");
                chat.setCreatedAt(LocalDateTime.now());
                
                // Save to database
                Chat savedChat = chatRepository.save(chat);
                log.info("Simple chat message saved successfully for farmer ID: {}", farmerId);
                
                return savedChat;
                
            } catch (Exception e) {
                log.error("Error in simple chat service: {}", e.getMessage());
                
                // Return a fallback response
                Chat fallbackChat = new Chat();
                fallbackChat.setId(1L);
                fallbackChat.setUserMessage(userMessage);
                fallbackChat.setAiResponse("Hello! I'm your AI farming assistant. I'm here to help you with agricultural questions. Please ask me about crops, irrigation, plant diseases, weather, or any farming-related topics.");
                fallbackChat.setMessageType(Chat.MessageType.valueOf(messageType.toUpperCase()));
                fallbackChat.setContextData("Fallback response");
                
                // Create a mock farmer for fallback
                Farmer mockFarmer = new Farmer();
                mockFarmer.setId(farmerId);
                mockFarmer.setName("Farmer " + farmerId);
                mockFarmer.setLocationName("Unknown");
                mockFarmer.setPreferredCrop("Unknown");
                fallbackChat.setFarmer(mockFarmer);
                
                return fallbackChat;
            }
        });
    }
    
    private String generateSimpleResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Simple keyword-based responses
        if (lowerMessage.contains("water") || lowerMessage.contains("irrigation")) {
            return "For irrigation, I recommend watering your crops early in the morning or late in the evening to reduce evaporation. " +
                   "The amount of water depends on your soil type and crop. Sandy soil needs more frequent watering, while clay soil retains water longer. " +
                   "Monitor soil moisture regularly and adjust based on weather conditions.";
        }
        
        if (lowerMessage.contains("disease") || lowerMessage.contains("pest") || lowerMessage.contains("sick")) {
            return "For plant diseases and pests, early detection is key. Look for unusual spots, wilting, or discoloration on leaves. " +
                   "Common solutions include: 1) Remove affected plant parts, 2) Improve air circulation, 3) Use organic treatments like neem oil, " +
                   "4) Ensure proper spacing between plants. If the problem persists, consider consulting a local agricultural extension service.";
        }
        
        if (lowerMessage.contains("weather") || lowerMessage.contains("rain") || lowerMessage.contains("temperature")) {
            return "Weather significantly affects farming. Monitor temperature, humidity, and rainfall patterns. " +
                   "During hot weather, increase watering frequency. Before heavy rain, avoid fertilizing. " +
                   "Use weather forecasts to plan your farming activities and protect crops from extreme conditions.";
        }
        
        if (lowerMessage.contains("soil") || lowerMessage.contains("fertilizer") || lowerMessage.contains("nutrient")) {
            return "Healthy soil is the foundation of good farming. Test your soil regularly for pH and nutrient levels. " +
                   "Add organic matter like compost to improve soil structure. Use fertilizers based on soil test results. " +
                   "Crop rotation helps maintain soil health and reduces pest problems.";
        }
        
        if (lowerMessage.contains("harvest") || lowerMessage.contains("yield") || lowerMessage.contains("crop")) {
            return "For better harvests, ensure proper spacing, adequate water, and nutrient management. " +
                   "Harvest at the right time - too early or too late can affect quality. " +
                   "Keep records of planting dates, treatments, and yields to improve future crops.";
        }
        
        if (lowerMessage.contains("help") || lowerMessage.contains("advice") || lowerMessage.contains("question")) {
            return "I'm here to help with your farming questions! I can assist with: " +
                   "• Irrigation and watering schedules\n" +
                   "• Plant disease identification and treatment\n" +
                   "• Soil health and fertilization\n" +
                   "• Weather-related farming decisions\n" +
                   "• Crop selection and management\n" +
                   "• Harvest timing and techniques\n\n" +
                   "What specific farming topic would you like to know more about?";
        }
        
        // Default response for general questions
        return "Thank you for your question! As your AI farming assistant, I'm here to help with agricultural topics. " +
               "I can provide advice on irrigation, plant diseases, soil health, weather impacts, crop management, and more. " +
               "Please feel free to ask me about any specific farming challenges you're facing, and I'll do my best to help you find solutions.";
    }
    
    public List<Chat> getChatHistory(Long farmerId) {
        try {
            log.info("Fetching simple chat history for farmer ID: {}", farmerId);
            return chatRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId);
        } catch (Exception e) {
            log.error("Error fetching chat history for farmer {}: {}", farmerId, e.getMessage());
            return new ArrayList<>();
        }
    }
}
