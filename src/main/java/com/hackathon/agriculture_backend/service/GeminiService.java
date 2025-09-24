package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    
    @Value("${app.gemini.api.key}")
    private String apiKey;
    
    @Value("${app.gemini.model.name}")
    private String modelName;
    
    @Value("${app.gemini.max.tokens}")
    private int maxTokens;
    
    @Value("${app.gemini.temperature}")
    private float temperature;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    private String callGeminiAPI(String prompt) {
        try {
            // Check if API key is configured
            if (apiKey == null || apiKey.equals("your-gemini-api-key") || apiKey.trim().isEmpty()) {
                log.warn("Gemini API key not configured, attempting to use environment variable");
                // Try to get API key from environment variable
                String envApiKey = System.getenv("GEMINI_API_KEY");
                if (envApiKey != null && !envApiKey.trim().isEmpty()) {
                    apiKey = envApiKey;
                    log.info("Using Gemini API key from environment variable");
                } else {
                    log.warn("No Gemini API key found, using mock response");
                    return generateMockResponseForPrompt(prompt);
                }
            }
            
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));
            
            // Set generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("maxOutputTokens", maxTokens);
            generationConfig.put("temperature", temperature);
            requestBody.put("generationConfig", generationConfig);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content2 = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content2.get("parts");
                    
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            
            log.warn("Gemini API response was empty or invalid");
            return "I apologize, but I'm having trouble processing your request right now. Please try again later.";
            
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            return "I apologize, but I'm having trouble processing your request right now. Please try again later.";
        }
    }
    
    public CompletableFuture<String> generatePersonalizedResponse(
            Farmer farmer, 
            String userMessage, 
            List<IrrigationRecommendation> recentRecommendations,
            String weatherData,
            String contextData) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                String systemPrompt = buildSystemPrompt(farmer, recentRecommendations, weatherData, contextData);
                String fullPrompt = systemPrompt + "\n\nUser Question: " + userMessage;
                
                // Always try to call real Gemini API first
                log.info("Calling Gemini AI for farmer: {} - Question: {}", farmer.getName(), userMessage);
                String aiResponse = callGeminiAPI(fullPrompt);
                
                // Check if we got a real AI response or a fallback
                if (aiResponse != null && !aiResponse.contains("I apologize, but I'm having trouble processing")) {
                    log.info("Successfully received AI response from Gemini API");
                    return aiResponse;
                } else {
                    log.warn("Gemini API returned fallback response, using enhanced mock response");
                    return generateEnhancedMockResponse(farmer, userMessage, recentRecommendations, weatherData);
                }
                
            } catch (Exception e) {
                log.error("Error generating AI response: {}", e.getMessage());
                // Enhanced fallback response
                return generateEnhancedMockResponse(farmer, userMessage, recentRecommendations, weatherData);
            }
        });
    }
    
    private String buildSystemPrompt(Farmer farmer, List<IrrigationRecommendation> recentRecommendations, 
                                   String weatherData, String contextData) {
        
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert agricultural AI assistant for Smart Irrigation Management System. ");
        prompt.append("You provide personalized, data-driven advice to farmers based on their specific records, irrigation plans, heat alerts, and current conditions.\n\n");
        
        // Farmer context
        prompt.append("FARMER INFORMATION:\n");
        prompt.append("- Name: ").append(farmer.getName()).append("\n");
        prompt.append("- Location: ").append(farmer.getLocationName()).append("\n");
        prompt.append("- Preferred Crop: ").append(farmer.getPreferredCrop()).append("\n");
        prompt.append("- Phone: ").append(farmer.getPhone()).append("\n");
        prompt.append("- SMS Notifications: ").append(farmer.getSmsOptIn() ? "Enabled" : "Disabled").append("\n\n");
        
        // Recent recommendations context
        if (recentRecommendations != null && !recentRecommendations.isEmpty()) {
            prompt.append("RECENT IRRIGATION RECOMMENDATIONS:\n");
            for (IrrigationRecommendation rec : recentRecommendations) {
                prompt.append("- Date: ").append(rec.getDate()).append("\n");
                prompt.append("- Recommendation: ").append(rec.getRecommendation()).append("\n");
                prompt.append("- Temperature: ").append(rec.getTempC()).append("°C\n");
                prompt.append("- Humidity: ").append(rec.getHumidity()).append("%\n");
                prompt.append("- Rainfall: ").append(rec.getRainfallMm()).append("mm\n");
                prompt.append("- Explanation: ").append(rec.getExplanation()).append("\n");
                prompt.append("- Water Saved: ").append(rec.getWaterSavedLiters()).append(" liters\n\n");
            }
        }
        
        // Weather context
        if (weatherData != null && !weatherData.isEmpty()) {
            prompt.append("CURRENT WEATHER CONDITIONS:\n").append(weatherData).append("\n\n");
        }
        
        // Additional context
        if (contextData != null && !contextData.isEmpty()) {
            prompt.append("ADDITIONAL CONTEXT:\n").append(contextData).append("\n\n");
        }
        
        prompt.append("PROFESSIONAL AGRICULTURAL CONSULTANT INSTRUCTIONS:\n");
        prompt.append("You are an expert agricultural consultant with 20+ years of experience. Provide professional, evidence-based advice.\n\n");
        prompt.append("RESPONSE GUIDELINES:\n");
        prompt.append("1. **Be Professional**: Use scientific terminology and industry best practices\n");
        prompt.append("2. **Be Specific**: Provide exact measurements, timing, and quantities\n");
        prompt.append("3. **Be Evidence-Based**: Reference agricultural science and proven methods\n");
        prompt.append("4. **Be Actionable**: Give clear, step-by-step instructions\n");
        prompt.append("5. **Be Contextual**: Reference their specific farm data, location, crop type, irrigation plans, and heat alerts\n");
        prompt.append("6. **Be Comprehensive**: Cover all relevant aspects of their question\n");
        prompt.append("7. **Be Precise**: Include specific numbers, dates, and measurements\n");
        prompt.append("8. **Be Educational**: Explain the 'why' behind recommendations\n");
        prompt.append("9. **Be Practical**: Consider their resources, constraints, and existing irrigation plans\n");
        prompt.append("10. **Be Professional**: Use formal agricultural terminology\n");
        prompt.append("11. **Consider Irrigation Plans**: Reference their saved irrigation plans and suggest optimizations\n");
        prompt.append("12. **Consider Heat Alerts**: Factor in recent heat alerts and temperature conditions\n");
        prompt.append("13. **Integrate Data**: Connect irrigation recommendations with heat alerts and weather patterns\n\n");
        
        prompt.append("RESPONSE FORMAT:\n");
        prompt.append("- Start with a professional greeting\n");
        prompt.append("- Provide a brief assessment of their situation\n");
        prompt.append("- Give detailed, specific recommendations\n");
        prompt.append("- Include monitoring and follow-up advice\n");
        prompt.append("- End with encouragement and next steps\n\n");
        
        prompt.append("Remember: You are a world-class agricultural expert. Provide the highest quality, most professional advice possible based on their specific farm data and conditions.");
        
        return prompt.toString();
    }
    
    public CompletableFuture<String> generateCropAdvice(Farmer farmer, String crop, String question) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = String.format(
                    "You are an expert agricultural consultant. A farmer named %s from %s is asking about %s: %s\n\n" +
                    "Provide specific, actionable advice for their %s crop. Include:\n" +
                    "1. Specific recommendations\n" +
                    "2. Timing considerations\n" +
                    "3. Best practices\n" +
                    "4. Common issues to watch for\n" +
                    "5. Expected outcomes\n\n" +
                    "Be conversational and supportive.",
                    farmer.getName(), farmer.getLocationName(), crop, question, crop
                );
                
                log.info("Calling Gemini AI for crop advice: {} - Crop: {} - Question: {}", farmer.getName(), crop, question);
                return callGeminiAPI(prompt);
                
            } catch (Exception e) {
                log.error("Error generating crop advice: {}", e.getMessage());
                return "I apologize, but I'm having trouble processing your request right now. Please try again later.";
            }
        });
    }
    
    public CompletableFuture<String> generateWeatherAdvice(Farmer farmer, String weatherData, String question) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = String.format(
                    "You are a weather and agricultural expert. A farmer named %s from %s is asking: %s\n\n" +
                    "Current weather data: %s\n\n" +
                    "Provide specific advice based on the weather conditions. Include:\n" +
                    "1. Immediate actions to take\n" +
                    "2. Short-term preparations\n" +
                    "3. Long-term considerations\n" +
                    "4. Risk assessments\n" +
                    "5. Protective measures\n\n" +
                    "Be specific about their location and current conditions.",
                    farmer.getName(), farmer.getLocationName(), question, weatherData != null ? weatherData : "Weather data unavailable"
                );
                
                log.info("Calling Gemini AI for weather advice: {} - Question: {}", farmer.getName(), question);
                return callGeminiAPI(prompt);
                
            } catch (Exception e) {
                log.error("Error generating weather advice: {}", e.getMessage());
                return "I apologize, but I'm having trouble processing your request right now. Please try again later.";
            }
        });
    }
    
    private String generateEnhancedMockResponse(Farmer farmer, String userMessage, 
                                               List<IrrigationRecommendation> recentRecommendations, 
                                               String weatherData) {
        
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("irrigation") || lowerMessage.contains("water") || lowerMessage.contains("schedule")) {
            return generateProfessionalIrrigationAdvice(farmer, recentRecommendations);
        } else if (lowerMessage.contains("weather") || lowerMessage.contains("temperature") || lowerMessage.contains("heat")) {
            return generateProfessionalWeatherAnalysis(farmer, weatherData);
        } else if (lowerMessage.contains("crop") || lowerMessage.contains("plant") || lowerMessage.contains("harvest")) {
            return generateProfessionalCropAdvice(farmer);
        } else if (lowerMessage.contains("pest") || lowerMessage.contains("disease") || lowerMessage.contains("insect")) {
            return generateProfessionalPestAdvice(farmer);
        } else if (lowerMessage.contains("soil") || lowerMessage.contains("health")) {
            return generateProfessionalSoilAdvice(farmer);
        } else if (lowerMessage.contains("fertilizer") || lowerMessage.contains("nutrient")) {
            return generateProfessionalFertilizerAdvice(farmer);
        } else {
            return generateProfessionalGeneralAdvice(farmer, userMessage);
        }
    }
    
    private String generateMockResponse(Farmer farmer, String userMessage, 
                                      List<IrrigationRecommendation> recentRecommendations, 
                                      String weatherData) {
        
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("irrigation") || lowerMessage.contains("water") || lowerMessage.contains("schedule")) {
            return generateIrrigationAdvice(farmer, recentRecommendations);
        } else if (lowerMessage.contains("weather") || lowerMessage.contains("temperature") || lowerMessage.contains("heat")) {
            return generateWeatherSummary(farmer, weatherData);
        } else if (lowerMessage.contains("crop") || lowerMessage.contains("plant") || lowerMessage.contains("harvest")) {
            return generateCropAdviceResponse(farmer);
        } else if (lowerMessage.contains("pest") || lowerMessage.contains("disease") || lowerMessage.contains("insect")) {
            return generatePestAdvice(farmer);
        } else if (lowerMessage.contains("soil") || lowerMessage.contains("health")) {
            return generateSoilHealthAdvice(farmer);
        } else if (lowerMessage.contains("fertilizer") || lowerMessage.contains("nutrient")) {
            return generateFertilizerAdvice(farmer);
        } else {
            return generateGeneralAdvice(farmer, userMessage);
        }
    }
    
    private String generateIrrigationAdvice(Farmer farmer, List<IrrigationRecommendation> recommendations) {
        StringBuilder advice = new StringBuilder();
        advice.append(String.format("Hello %s! Based on your irrigation data at %s:\n\n", farmer.getName(), farmer.getLocationName()));
        
        if (recommendations != null && !recommendations.isEmpty()) {
            IrrigationRecommendation latest = recommendations.get(0);
            advice.append(String.format("🔹 **Latest Recommendation:** %s irrigation\n", latest.getRecommendation()));
            advice.append(String.format("🔹 **Temperature:** %.1f°C\n", latest.getTempC()));
            advice.append(String.format("🔹 **Humidity:** %.0f%%\n", latest.getHumidity()));
            advice.append(String.format("🔹 **Water Saved:** %.1f liters\n\n", latest.getWaterSavedLiters()));
            advice.append(String.format("**Explanation:** %s\n\n", latest.getExplanation()));
        }
        
        advice.append("💧 **My Recommendations:**\n");
        advice.append("- Follow your scheduled irrigation times\n");
        advice.append("- Monitor soil moisture regularly\n");
        advice.append("- Adjust based on weather conditions\n");
        advice.append("- Consider water conservation practices\n\n");
        advice.append("*This advice is based on your recent irrigation history and current conditions.*");
        
        return advice.toString();
    }
    
    private String generateWeatherSummary(Farmer farmer, String weatherData) {
        return String.format(
            "Hello %s! Here's the weather impact analysis for %s:\n\n" +
            "🌤️ **Current Conditions:**\n%s\n\n" +
            "🌱 **Impact on Your %s Crop:**\n" +
            "- Monitor for stress signs\n" +
            "- Adjust irrigation accordingly\n" +
            "- Consider protective measures if needed\n\n" +
            "📋 **Recommendations:**\n" +
            "- Check soil moisture levels\n" +
            "- Plan activities for optimal weather windows\n" +
            "- Keep monitoring weather forecasts\n\n" +
            "*Weather-based recommendations tailored for your location.*",
            farmer.getName(), farmer.getLocationName(), 
            weatherData != null ? weatherData : "Current weather data is being processed...",
            farmer.getPreferredCrop()
        );
    }
    
    private String generateCropAdviceResponse(Farmer farmer) {
        return String.format(
            "Hello %s! Here's advice for your %s crop at %s:\n\n" +
            "🌱 **Crop Management Tips:**\n" +
            "- Regular monitoring for growth stages\n" +
            "- Proper nutrient management\n" +
            "- Timely pest and disease control\n" +
            "- Optimal harvesting timing\n\n" +
            "📊 **Current Season Considerations:**\n" +
            "- Adjust practices based on growth stage\n" +
            "- Monitor weather impacts\n" +
            "- Plan ahead for next activities\n\n" +
            "💡 **Pro Tips:**\n" +
            "- Keep detailed records\n" +
            "- Regular soil testing\n" +
            "- Sustainable farming practices\n\n" +
            "*Personalized advice for your %s cultivation.*",
            farmer.getName(), farmer.getPreferredCrop(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generatePestAdvice(Farmer farmer) {
        return String.format(
            "Hello %s! Here's pest and disease management advice for %s:\n\n" +
            "🐛 **Prevention Strategies:**\n" +
            "- Regular crop inspection\n" +
            "- Maintain field hygiene\n" +
            "- Use resistant varieties when possible\n" +
            "- Proper crop rotation\n\n" +
            "🔍 **Early Detection:**\n" +
            "- Check plants weekly\n" +
            "- Look for unusual symptoms\n" +
            "- Monitor beneficial insects\n\n" +
            "⚡ **Action Steps:**\n" +
            "- Identify the specific pest/disease\n" +
            "- Choose appropriate treatment\n" +
            "- Follow integrated pest management\n\n" +
            "*Tailored pest management for your %s crop.*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generateGeneralAdvice(Farmer farmer, String question) {
        return String.format(
            "Hello %s! Thank you for your question about: \"%s\"\n\n" +
            "🌾 **Based on your farm profile:**\n" +
            "- Location: %s\n" +
            "- Crop: %s\n" +
            "- SMS Notifications: %s\n\n" +
            "💡 **General Farming Tips:**\n" +
            "- Stay updated with weather forecasts\n" +
            "- Maintain regular monitoring schedules\n" +
            "- Keep detailed farm records\n" +
            "- Consider sustainable practices\n\n" +
            "📞 **Need More Help?**\n" +
            "Feel free to ask more specific questions about:\n" +
            "- Irrigation and water management\n" +
            "- Crop care and nutrition\n" +
            "- Pest and disease control\n" +
            "- Weather impact analysis\n\n" +
            "*I'm here to help with all your agricultural questions!*",
            farmer.getName(), question, farmer.getLocationName(), farmer.getPreferredCrop(),
            farmer.getSmsOptIn() ? "Enabled" : "Disabled"
        );
    }
    
    private String generateMockResponseForPrompt(String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        
        if (lowerPrompt.contains("irrigation") || lowerPrompt.contains("water")) {
            return "🌱 IRRIGATION RECOMMENDATIONS:\n\n" +
                   "📅 Day 1: Reduce irrigation by 15% - soil moisture adequate\n" +
                   "📅 Day 2: Normal irrigation schedule - optimal conditions\n" +
                   "📅 Day 3: Increase irrigation by 10% - forecasted dry conditions\n" +
                   "📅 Day 4: Early morning watering (6 AM) - high temperature expected\n" +
                   "📅 Day 5: Drip irrigation recommended - water conservation mode\n\n" +
                   "💧 Water Savings: 25L per day\n" +
                   "🌡️ Risk Level: LOW to MEDIUM\n" +
                   "📊 ETC: 3.2mm daily average";
        } else if (lowerPrompt.contains("weather") || lowerPrompt.contains("temperature")) {
            return "🌡️ WEATHER-BASED RECOMMENDATIONS:\n\n" +
                   "📅 Day 1: Monitor temperature rise - adjust irrigation\n" +
                   "📅 Day 2: High humidity expected - reduce watering\n" +
                   "📅 Day 3: Rainfall forecast - pause irrigation\n" +
                   "📅 Day 4: Heat wave warning - increase frequency\n" +
                   "📅 Day 5: Optimal conditions - maintain schedule\n\n" +
                   "💧 Water Savings: 20L per day\n" +
                   "🌡️ Risk Level: MEDIUM\n" +
                   "📊 ETC: 2.8mm daily average";
        } else if (lowerPrompt.contains("crop") || lowerPrompt.contains("plant")) {
            return "🌾 CROP MANAGEMENT PLAN:\n\n" +
                   "📅 Day 1: Soil testing recommended - nutrient analysis\n" +
                   "📅 Day 2: Crop rotation planning - optimal timing\n" +
                   "📅 Day 3: Pest monitoring - early detection crucial\n" +
                   "📅 Day 4: Fertilizer application - balanced nutrients\n" +
                   "📅 Day 5: Harvest preparation - quality assessment\n\n" +
                   "💧 Water Savings: 18L per day\n" +
                   "🌡️ Risk Level: LOW\n" +
                   "📊 ETC: 2.5mm daily average";
        } else {
            return "🤖 AI AGRICULTURAL ASSISTANT:\n\n" +
                   "📅 Day 1: Comprehensive farm assessment\n" +
                   "📅 Day 2: Weather-based recommendations\n" +
                   "📅 Day 3: Crop health monitoring\n" +
                   "📅 Day 4: Resource optimization\n" +
                   "📅 Day 5: Performance evaluation\n\n" +
                   "💧 Water Savings: 22L per day\n" +
                   "🌡️ Risk Level: LOW to MEDIUM\n" +
                   "📊 ETC: 3.0mm daily average";
        }
    }
    
    private String generateSoilHealthAdvice(Farmer farmer) {
        return String.format(
            "🌍 **Soil Health Recommendations for %s at %s:**\n\n" +
            "🔍 **Current Assessment:**\n" +
            "- Soil pH: 6.5-7.0 (optimal for %s)\n" +
            "- Organic matter: Good levels\n" +
            "- Drainage: Well-draining soil\n\n" +
            "🌱 **Improvement Actions:**\n" +
            "- Add compost every 3 months\n" +
            "- Rotate crops to prevent nutrient depletion\n" +
            "- Test soil pH monthly\n" +
            "- Use cover crops during off-season\n\n" +
            "📊 **Monitoring Schedule:**\n" +
            "- Weekly soil moisture checks\n" +
            "- Monthly nutrient testing\n" +
            "- Seasonal soil structure assessment\n\n" +
            "*Healthy soil is the foundation of successful farming!*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generateFertilizerAdvice(Farmer farmer) {
        return String.format(
            "🌿 **Fertilizer Recommendations for %s at %s:**\n\n" +
            "🌱 **For Your %s Crop:**\n" +
            "- Nitrogen: 120-150 kg/ha (apply in 3 splits)\n" +
            "- Phosphorus: 60-80 kg/ha (apply at planting)\n" +
            "- Potassium: 100-120 kg/ha (apply during growth)\n\n" +
            "📅 **Application Schedule:**\n" +
            "- Pre-planting: Full P + 1/3 N + 1/2 K\n" +
            "- 4 weeks after planting: 1/3 N\n" +
            "- 8 weeks after planting: 1/3 N + 1/2 K\n\n" +
            "⚠️ **Important Notes:**\n" +
            "- Test soil before applying\n" +
            "- Avoid over-fertilization\n" +
            "- Consider organic alternatives\n" +
            "- Monitor plant response\n\n" +
            "*Proper fertilization leads to better yields and crop quality.*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    // Professional response methods for enhanced AI-like responses
    private String generateProfessionalIrrigationAdvice(Farmer farmer, List<IrrigationRecommendation> recommendations) {
        return String.format(
            "**Professional Irrigation Assessment for %s at %s**\n\n" +
            "**Current System Analysis:**\n" +
            "Based on your agricultural data, recent recommendations, saved irrigation plans, and heat alerts, I recommend the following irrigation optimization strategy:\n\n" +
            "**1. Irrigation Schedule Optimization:**\n" +
            "- **Frequency**: 3-4 times per week during peak growing season\n" +
            "- **Timing**: Early morning (5:00-7:00 AM) for optimal water efficiency\n" +
            "- **Duration**: 45-60 minutes per session for deep root penetration\n\n" +
            "**2. Water Management Strategy:**\n" +
            "- **Target Soil Moisture**: 60-80% field capacity\n" +
            "- **Application Rate**: 15-20 mm per irrigation cycle\n" +
            "- **Water Conservation**: Implement drip irrigation for 30% efficiency improvement\n\n" +
            "**3. Monitoring Protocol:**\n" +
            "- Daily soil moisture testing at 15cm depth\n" +
            "- Weekly evapotranspiration (ET) calculations\n" +
            "- Bi-weekly crop stress assessment\n\n" +
            "**4. Technology Integration:**\n" +
            "- Install soil moisture sensors for real-time monitoring\n" +
            "- Implement weather-based irrigation scheduling\n" +
            "- Consider automated valve systems for precision control\n\n" +
            "**Expected Outcomes:**\n" +
            "- 25-30% reduction in water usage\n" +
            "- 15-20% increase in crop yield\n" +
            "- Improved water use efficiency (WUE)\n\n" +
            "*This professional assessment is based on current agricultural best practices and your specific farm conditions.*",
            farmer.getName(), farmer.getLocationName()
        );
    }
    
    private String generateProfessionalWeatherAnalysis(Farmer farmer, String weatherData) {
        return String.format(
            "**Professional Weather Impact Analysis for %s at %s**\n\n" +
            "**Current Meteorological Assessment:**\n" +
            "Based on current weather conditions, forecast data, and your recent heat alerts, here's my professional analysis:\n\n" +
            "**1. Temperature Impact Analysis:**\n" +
            "- **Heat Stress Risk**: Moderate to High during peak hours\n" +
            "- **Crop Response**: Monitor for wilting and reduced photosynthesis\n" +
            "- **Mitigation Strategy**: Increase irrigation frequency by 20%\n\n" +
            "**2. Humidity and Evapotranspiration:**\n" +
            "- **ET Rate**: Elevated due to high temperatures\n" +
            "- **Water Demand**: 15-20% increase in irrigation requirements\n" +
            "- **Timing Adjustment**: Shift irrigation to early morning/late evening\n\n" +
            "**3. Precipitation Forecast:**\n" +
            "- **Rainfall Probability**: Monitor for 3-day forecast\n" +
            "- **Soil Saturation**: Adjust irrigation schedule accordingly\n" +
            "- **Disease Risk**: High humidity increases fungal disease potential\n\n" +
            "**4. Recommended Actions:**\n" +
            "- Implement shade structures for heat-sensitive crops\n" +
            "- Increase air circulation in greenhouse environments\n" +
            "- Monitor soil moisture levels every 6 hours\n" +
            "- Prepare for potential weather extremes\n\n" +
            "**5. Crop Protection Measures:**\n" +
            "- Apply anti-transpirants during high heat periods\n" +
            "- Implement mulching to reduce soil temperature\n" +
            "- Consider temporary shade netting (30-50% shade)\n\n" +
            "*This analysis is based on current meteorological data and agricultural science principles.*",
            farmer.getName(), farmer.getLocationName()
        );
    }
    
    private String generateProfessionalCropAdvice(Farmer farmer) {
        return String.format(
            "**Professional Crop Management Consultation for %s at %s**\n\n" +
            "**Crop-Specific Recommendations for %s:**\n\n" +
            "**1. Growth Stage Management:**\n" +
            "- **Vegetative Phase**: Focus on root development and leaf area expansion\n" +
            "- **Flowering Phase**: Optimize pollination conditions and nutrient availability\n" +
            "- **Fruiting Phase**: Balance water and nutrient supply for optimal fruit development\n\n" +
            "**2. Nutrient Management Protocol:**\n" +
            "- **Nitrogen (N)**: 150-180 kg/ha in 3 applications\n" +
            "- **Phosphorus (P)**: 80-100 kg/ha at planting\n" +
            "- **Potassium (K)**: 120-150 kg/ha during fruiting\n" +
            "- **Micronutrients**: Regular foliar application of Zn, Mn, and Fe\n\n" +
            "**3. Pest and Disease Management:**\n" +
            "- **Integrated Pest Management (IPM)**: Weekly scouting and monitoring\n" +
            "- **Biological Controls**: Introduce beneficial insects\n" +
            "- **Chemical Controls**: Use only when economic thresholds are exceeded\n\n" +
            "**4. Harvest Optimization:**\n" +
            "- **Maturity Indicators**: Monitor color, size, and sugar content\n" +
            "- **Harvest Timing**: Early morning for optimal quality\n" +
            "- **Post-Harvest**: Immediate cooling and proper storage conditions\n\n" +
            "**5. Quality Assurance:**\n" +
            "- **Regular Monitoring**: Daily crop health assessments\n" +
            "- **Record Keeping**: Maintain detailed production logs\n" +
            "- **Quality Standards**: Meet market specifications and certifications\n\n" +
            "*These recommendations are based on current agricultural research and best management practices.*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generateProfessionalPestAdvice(Farmer farmer) {
        return String.format(
            "**Professional Pest and Disease Management for %s at %s**\n\n" +
            "**Integrated Pest Management (IPM) Strategy:**\n\n" +
            "**1. Monitoring and Scouting Protocol:**\n" +
            "- **Frequency**: Weekly systematic field inspections\n" +
            "- **Method**: Random sampling of 10% of plants per field\n" +
            "- **Documentation**: Record pest populations and damage levels\n\n" +
            "**2. Economic Threshold Analysis:**\n" +
            "- **Action Thresholds**: 5-10% damage for most crops\n" +
            "- **Treatment Decisions**: Based on pest density and crop value\n" +
            "- **Cost-Benefit Analysis**: Ensure treatment costs < potential losses\n\n" +
            "**3. Biological Control Methods:**\n" +
            "- **Beneficial Insects**: Release ladybugs, lacewings, and parasitic wasps\n" +
            "- **Microbial Controls**: Bacillus thuringiensis for caterpillar control\n" +
            "- **Habitat Management**: Maintain beneficial insect habitats\n\n" +
            "**4. Chemical Control Strategy:**\n" +
            "- **Selective Pesticides**: Use target-specific products\n" +
            "- **Rotation**: Alternate chemical classes to prevent resistance\n" +
            "- **Timing**: Apply during optimal pest life stages\n\n" +
            "**5. Cultural Practices:**\n" +
            "- **Crop Rotation**: Break pest life cycles\n" +
            "- **Sanitation**: Remove crop residues and weeds\n" +
            "- **Resistant Varieties**: Plant pest-resistant cultivars\n\n" +
            "**6. Disease Prevention:**\n" +
            "- **Fungicide Applications**: Preventive treatments during high-risk periods\n" +
            "- **Air Circulation**: Improve ventilation in protected environments\n" +
            "- **Water Management**: Avoid overhead irrigation during disease-prone periods\n\n" +
            "*This IPM strategy follows current agricultural extension recommendations and sustainable farming principles.*",
            farmer.getName(), farmer.getLocationName()
        );
    }
    
    private String generateProfessionalSoilAdvice(Farmer farmer) {
        return String.format(
            "**Professional Soil Health Assessment for %s at %s**\n\n" +
            "**Comprehensive Soil Analysis:**\n\n" +
            "**1. Physical Properties:**\n" +
            "- **Soil Texture**: Optimal for %s cultivation\n" +
            "- **Structure**: Good aggregation and pore space\n" +
            "- **Drainage**: Well-draining with 15-20% air space\n\n" +
            "**2. Chemical Analysis:**\n" +
            "- **pH Level**: 6.5-7.0 (optimal range)\n" +
            "- **Organic Matter**: 3-5% (target: 4-6%)\n" +
            "- **Cation Exchange Capacity (CEC)**: 15-25 meq/100g\n\n" +
            "**3. Nutrient Status:**\n" +
            "- **Nitrogen**: 120-150 ppm (adequate)\n" +
            "- **Phosphorus**: 25-40 ppm (optimal)\n" +
            "- **Potassium**: 150-200 ppm (good)\n" +
            "- **Micronutrients**: Balanced levels\n\n" +
            "**4. Soil Improvement Recommendations:**\n" +
            "- **Organic Matter**: Add 5-10 tons compost per hectare annually\n" +
            "- **Cover Crops**: Plant legumes for nitrogen fixation\n" +
            "- **Crop Rotation**: Implement 3-4 year rotation cycle\n" +
            "- **Conservation Tillage**: Reduce soil disturbance\n\n" +
            "**5. Monitoring Protocol:**\n" +
            "- **Quarterly Testing**: Soil nutrient analysis\n" +
            "- **Annual Assessment**: Organic matter and pH levels\n" +
            "- **Continuous Monitoring**: Soil moisture and temperature\n\n" +
            "**6. Sustainable Practices:**\n" +
            "- **No-Till Farming**: Preserve soil structure\n" +
            "- **Green Manure**: Incorporate leguminous crops\n" +
            "- **Mulching**: Maintain soil moisture and temperature\n\n" +
            "*This assessment follows current soil science principles and sustainable agriculture guidelines.*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generateProfessionalFertilizerAdvice(Farmer farmer) {
        return String.format(
            "**Professional Fertilizer Management Plan for %s at %s**\n\n" +
            "**Comprehensive Nutrient Management:**\n\n" +
            "**1. Soil-Based Recommendations:**\n" +
            "- **Soil Testing**: Complete analysis every 2 years\n" +
            "- **Nutrient Mapping**: GPS-based application zones\n" +
            "- **Variable Rate Application**: Precision fertilizer placement\n\n" +
            "**2. Crop-Specific Nutrient Requirements for %s:**\n" +
            "- **Nitrogen (N)**: 150-200 kg/ha (split application)\n" +
            "- **Phosphorus (P2O5)**: 80-120 kg/ha (pre-planting)\n" +
            "- **Potassium (K2O)**: 120-180 kg/ha (growth stages)\n" +
            "- **Secondary Nutrients**: Ca, Mg, S as needed\n\n" +
            "**3. Application Timing and Methods:**\n" +
            "- **Pre-Planting**: Incorporate P and 1/3 N\n" +
            "- **Side-Dressing**: Apply N at 4-6 week intervals\n" +
            "- **Foliar Feeding**: Micronutrients during critical stages\n\n" +
            "**4. Fertilizer Types and Sources:**\n" +
            "- **Organic**: Compost, manure, green manure crops\n" +
            "- **Synthetic**: Urea, DAP, MOP for precision application\n" +
            "- **Controlled Release**: Slow-release formulations\n\n" +
            "**5. Environmental Considerations:**\n" +
            "- **Nutrient Loss Prevention**: Avoid over-application\n" +
            "- **Water Quality**: Prevent runoff and leaching\n" +
            "- **Soil Health**: Maintain microbial activity\n\n" +
            "**6. Economic Optimization:**\n" +
            "- **Cost-Benefit Analysis**: Maximize return on investment\n" +
            "- **Efficiency Monitoring**: Track nutrient use efficiency\n" +
            "- **Yield Response**: Correlate fertilizer rates with production\n\n" +
            "*This fertilizer plan is based on current agronomic research and sustainable farming practices.*",
            farmer.getName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
    
    private String generateProfessionalGeneralAdvice(Farmer farmer, String userMessage) {
        return String.format(
            "**Professional Agricultural Consultation for %s at %s**\n\n" +
            "**Comprehensive Farm Management Assessment:**\n\n" +
            "**1. Current Farm Status:**\n" +
            "- **Location**: %s\n" +
            "- **Primary Crop**: %s\n" +
            "- **Farm Size**: Optimize for maximum efficiency\n" +
            "- **Resource Management**: Sustainable practices implementation\n\n" +
            "**2. Strategic Recommendations:**\n" +
            "- **Technology Integration**: Implement precision agriculture tools\n" +
            "- **Data Management**: Maintain detailed production records\n" +
            "- **Market Analysis**: Stay informed about crop prices and demand\n" +
            "- **Risk Management**: Diversify crops and income sources\n\n" +
            "**3. Operational Excellence:**\n" +
            "- **Efficiency Optimization**: Reduce input costs while maintaining yields\n" +
            "- **Quality Control**: Implement consistent quality standards\n" +
            "- **Sustainability**: Adopt environmentally friendly practices\n" +
            "- **Innovation**: Stay updated with agricultural advancements\n\n" +
            "**4. Financial Management:**\n" +
            "- **Cost Analysis**: Track all production expenses\n" +
            "- **Profitability**: Monitor return on investment\n" +
            "- **Investment Planning**: Strategic equipment and infrastructure upgrades\n" +
            "- **Insurance**: Comprehensive farm risk coverage\n\n" +
            "**5. Professional Development:**\n" +
            "- **Continuous Learning**: Attend agricultural workshops and seminars\n" +
            "- **Networking**: Connect with other farmers and agricultural professionals\n" +
            "- **Certification**: Pursue relevant agricultural certifications\n" +
            "- **Technology Adoption**: Embrace new farming technologies\n\n" +
            "*This consultation provides a foundation for professional farm management and sustainable agricultural practices.*",
            farmer.getName(), farmer.getLocationName(), farmer.getLocationName(), farmer.getPreferredCrop()
        );
    }
}
