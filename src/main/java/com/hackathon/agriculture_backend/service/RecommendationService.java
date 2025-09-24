package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.RecommendationResult;
import com.hackathon.agriculture_backend.dto.RecommendationRequestDto;
import com.hackathon.agriculture_backend.dto.RecommendationResponseDto;
import com.hackathon.agriculture_backend.dto.WeatherDto;
import com.hackathon.agriculture_backend.model.Crop;
import com.hackathon.agriculture_backend.model.Farmer;
import com.hackathon.agriculture_backend.model.IrrigationRecommendation;
import com.hackathon.agriculture_backend.repository.CropRepository;
import com.hackathon.agriculture_backend.repository.IrrigationRecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {
    
    private final CropRepository cropRepository;
    private final IrrigationRecommendationRepository recommendationRepository;
    private final WeatherService weatherService;
    private final GeminiService geminiService;
    
    public RecommendationResult calculateRecommendation(Farmer farmer, Double tempC, Double humidity, 
                                                     Double rainfall, Double forecastRain, Double et0) {
        log.info("Calculating recommendation for farmer: {} at location: {}", 
                farmer.getName(), farmer.getLocationName());
        
        // Get crop information
        Crop crop = cropRepository.findByName(farmer.getPreferredCrop())
                .orElseGet(() -> getDefaultCrop(farmer.getPreferredCrop()));
        
        int score = 0;
        StringBuilder explanation = new StringBuilder();
        
        // Enhanced temperature scoring with more precise thresholds
        if (tempC > 40) {
            score += 3; // Extreme heat
            explanation.append("Extreme temperature (").append(String.format("%.1f", tempC)).append("°C) requires urgent irrigation. ");
        } else if (tempC > 35) {
            score += 2; // High heat
            explanation.append("High temperature (").append(String.format("%.1f", tempC)).append("°C) requires more irrigation. ");
        } else if (tempC > 28) {
            score += 1; // Moderate heat
            explanation.append("Moderate temperature (").append(String.format("%.1f", tempC)).append("°C) needs some irrigation. ");
        } else if (tempC < 15) {
            score -= 1; // Cool weather reduces irrigation needs
            explanation.append("Cool temperature (").append(String.format("%.1f", tempC)).append("°C) reduces irrigation needs. ");
        } else {
            explanation.append("Optimal temperature (").append(String.format("%.1f", tempC)).append("°C) has normal irrigation needs. ");
        }
        
        // Enhanced humidity scoring
        if (humidity < 20) {
            score += 3; // Very dry
            explanation.append("Very low humidity (").append(String.format("%.0f", humidity)).append("%) significantly increases water demand. ");
        } else if (humidity < 30) {
            score += 2; // Dry
            explanation.append("Low humidity (").append(String.format("%.0f", humidity)).append("%) increases water demand. ");
        } else if (humidity < 50) {
            score += 1; // Moderate
            explanation.append("Moderate humidity (").append(String.format("%.0f", humidity)).append("%) has normal water needs. ");
        } else if (humidity > 80) {
            score -= 1; // High humidity reduces needs
            explanation.append("High humidity (").append(String.format("%.0f", humidity)).append("%) reduces water demand. ");
        } else {
            explanation.append("Good humidity (").append(String.format("%.0f", humidity)).append("%) has optimal water needs. ");
        }
        
        // Enhanced rainfall scoring with better logic
        Double totalRainfall = rainfall + (forecastRain != null ? forecastRain : 0.0);
        if (totalRainfall < 1) {
            score += 3; // No rain
            explanation.append("No significant rainfall (").append(String.format("%.1f", rainfall)).append("mm) requires irrigation. ");
        } else if (totalRainfall < 3) {
            score += 2; // Light rain
            explanation.append("Light rainfall (").append(String.format("%.1f", rainfall)).append("mm) may need some irrigation. ");
        } else if (totalRainfall < 10) {
            score += 1; // Moderate rain
            explanation.append("Moderate rainfall (").append(String.format("%.1f", rainfall)).append("mm) reduces irrigation needs. ");
        } else {
            score -= 1; // Heavy rain
            explanation.append("Adequate rainfall (").append(String.format("%.1f", rainfall)).append("mm) significantly reduces irrigation needs. ");
        }
        
        // Enhanced evapotranspiration scoring
        if (et0 > crop.getEtThreshold() * 1.5) {
            score += 2; // Very high ET
            explanation.append("Very high evapotranspiration (").append(String.format("%.2f", et0)).append("mm) significantly increases water loss. ");
        } else if (et0 > crop.getEtThreshold()) {
            score += 1; // High ET
            explanation.append("High evapotranspiration (").append(String.format("%.2f", et0)).append("mm) increases water loss. ");
        } else {
            explanation.append("Normal evapotranspiration (").append(String.format("%.2f", et0)).append("mm) has standard water needs. ");
        }
        
        // Determine recommendation with enhanced logic
        String recommendation;
        if (score >= 6) {
            recommendation = "HIGH";
        } else if (score >= 3) {
            recommendation = "MODERATE";
        } else {
            recommendation = "LOW";
        }
        
        // Calculate water savings (assuming 1 hectare farm)
        Double waterSavedLiters = calculateWaterSavings(recommendation, crop, 1.0);
        
        RecommendationResult result = new RecommendationResult(recommendation, explanation.toString());
        result.setScore(score);
        result.setWaterSavedLiters(waterSavedLiters);
        
        log.info("Enhanced recommendation calculated: {} (Score: {})", recommendation, score);
        
        return result;
    }
    
    public IrrigationRecommendation saveRecommendation(Farmer farmer, LocalDate date, String cropType,
                                                     String locationName, Double tempC, Double humidity,
                                                     Double rainfallMm, Double evapotranspiration,
                                                     RecommendationResult result) {
        log.info("Saving recommendation for farmer: {} on date: {}", farmer.getName(), date);
        
        IrrigationRecommendation recommendation = new IrrigationRecommendation();
        recommendation.setFarmer(farmer);
        recommendation.setDate(date);
        recommendation.setCropType(cropType);
        recommendation.setLocationName(locationName);
        recommendation.setTempC(tempC);
        recommendation.setHumidity(humidity);
        recommendation.setRainfallMm(rainfallMm);
        recommendation.setEvapotranspiration(evapotranspiration);
        recommendation.setRecommendation(result.getRecommendation());
        recommendation.setExplanation(result.getExplanation());
        recommendation.setWaterSavedLiters(result.getWaterSavedLiters());
        
        IrrigationRecommendation savedRecommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation saved successfully with ID: {}", savedRecommendation.getId());
        
        return savedRecommendation;
    }
    
    public Optional<IrrigationRecommendation> getRecommendationForFarmerAndDate(Long farmerId, LocalDate date) {
        return recommendationRepository.findByFarmerIdAndDate(farmerId, date).stream().findFirst();
    }
    
    public boolean hasRecommendationForDate(Long farmerId, LocalDate date) {
        return !recommendationRepository.findByFarmerIdAndDate(farmerId, date).isEmpty();
    }
    
    private Crop getDefaultCrop(String cropName) {
        log.warn("Crop '{}' not found in database, using default values", cropName);
        
        // Default crop values based on common crops
        Crop defaultCrop = new Crop();
        defaultCrop.setName(cropName);
        defaultCrop.setWaterDemandLitersPerHectare(5000.0); // Default 5000 L/hectare
        defaultCrop.setEtThreshold(5.0); // Default ET threshold
        defaultCrop.setIsActive(true);
        
        return defaultCrop;
    }
    
    private Double calculateWaterSavings(String recommendation, Crop crop, Double hectares) {
        // Base water demand per hectare
        Double baseWaterDemand = crop.getWaterDemandLitersPerHectare() * hectares;
        
        // Calculate savings based on recommendation
        Double savingsMultiplier;
        switch (recommendation) {
            case "LOW":
                savingsMultiplier = 0.3; // 30% of base demand
                break;
            case "MODERATE":
                savingsMultiplier = 0.6; // 60% of base demand
                break;
            case "HIGH":
                savingsMultiplier = 1.0; // 100% of base demand (no savings)
                break;
            default:
                savingsMultiplier = 0.5; // Default 50% savings
        }
        
        // Calculate water saved (difference between base demand and actual usage)
        Double actualUsage = baseWaterDemand * savingsMultiplier;
        Double waterSaved = baseWaterDemand - actualUsage;
        
        return Math.max(0.0, waterSaved); // Ensure non-negative savings
    }
    
    /**
     * Generate AI-powered recommendations based on zone, crop, and weather data
     */
    public List<RecommendationResponseDto> generateAIRecommendations(RecommendationRequestDto request) {
        log.info("Generating AI recommendations for farmer: {}, zone: {}, category: {}", 
                request.getFarmerId(), request.getZoneId(), request.getCategory());
        
        try {
            // Get weather data for the zone
            WeatherDto weatherData = weatherService.getIrrigationWeatherData(
                    request.getLatitude(), request.getLongitude());
            
            // Generate AI prompt
            String prompt = buildAIRecommendationPrompt(request, weatherData);
            
            // Get farmer object for AI service
            Farmer farmer = new Farmer();
            farmer.setId(request.getFarmerId());
            farmer.setName("Farmer " + request.getFarmerId());
            farmer.setLocationName(request.getZoneName());
            farmer.setPreferredCrop(request.getCropType());
            
            // Get AI response using correct method signature
            String aiResponse = geminiService.generatePersonalizedResponse(
                    farmer, prompt, new ArrayList<>(), 
                    "Weather: " + weatherData.getTempC() + "°C, " + weatherData.getHumidity() + "% humidity", 
                    "Zone: " + request.getZoneName() + ", Crop: " + request.getCropType()).join();
            
            // Parse AI response into structured recommendations
            List<RecommendationResponseDto> recommendations = parseAIResponseToRecommendations(
                    aiResponse, request, weatherData);
            
            log.info("Generated {} AI recommendations", recommendations.size());
            return recommendations;
            
        } catch (Exception e) {
            log.error("Error generating AI recommendations: {}", e.getMessage());
            // Return empty list instead of mock data - force real data only
            throw new RuntimeException("Failed to generate AI recommendations: " + e.getMessage());
        }
    }
    
    /**
     * Get recommendations for a specific farmer
     */
    public List<RecommendationResponseDto> getFarmerRecommendations(Long farmerId, String category, String zoneId) {
        log.info("Fetching recommendations for farmer: {}, category: {}, zone: {}", farmerId, category, zoneId);
        
        List<IrrigationRecommendation> recommendations = recommendationRepository.findByFarmerIdOrderByDateDesc(farmerId);
        
        return recommendations.stream()
                .filter(rec -> category == null || rec.getRecommendation().toLowerCase().contains(category.toLowerCase()))
                .filter(rec -> zoneId == null || rec.getLocationName().equals(zoneId))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recommendations for a specific zone
     */
    public List<RecommendationResponseDto> getZoneRecommendations(Long zoneId, String category) {
        log.info("Fetching recommendations for zone: {}, category: {}", zoneId, category);
        
        // This would need a zone-based query - for now return empty list
        return new ArrayList<>();
    }
    
    private String buildAIRecommendationPrompt(RecommendationRequestDto request, WeatherDto weatherData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate ").append(request.getCategory()).append(" recommendations for ")
               .append(request.getCropType()).append(" in zone ").append(request.getZoneName())
               .append(" (").append(request.getLatitude()).append(", ").append(request.getLongitude())
               .append(") from ").append(request.getStartDate()).append(" to ").append(request.getEndDate()).append(".\n\n");
        
        prompt.append("Current weather conditions:\n");
        prompt.append("- Temperature: ").append(weatherData.getTempC()).append("°C\n");
        prompt.append("- Humidity: ").append(weatherData.getHumidity()).append("%\n");
        prompt.append("- Rainfall: ").append(weatherData.getRainfallMm()).append("mm\n");
        prompt.append("- Forecast Rainfall: ").append(weatherData.getForecastRainfallMm()).append("mm\n");
        prompt.append("- Heat Alert: ").append(weatherData.getHeatAlert() ? "Yes" : "No").append("\n");
        prompt.append("- Weather Description: ").append(weatherData.getWeatherDescription()).append("\n\n");
        
        prompt.append("Provide specific, actionable recommendations for each day in the date range. ");
        prompt.append("Include water savings potential, risk levels (LOW/MEDIUM/HIGH), and detailed explanations. ");
        prompt.append("Format each recommendation with: Date, Risk Level, Recommendation Text, Water Saved (L), Temperature, Humidity, Rainfall, ETC.");
        
        return prompt.toString();
    }
    
    private List<RecommendationResponseDto> parseAIResponseToRecommendations(
            String aiResponse, RecommendationRequestDto request, WeatherDto weatherData) {
        
        List<RecommendationResponseDto> recommendations = new ArrayList<>();
        
        // Simple parsing - in production, use more sophisticated NLP
        String[] lines = aiResponse.split("\n");
        LocalDate currentDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        
        int lineIndex = 0;
        while (!currentDate.isAfter(endDate) && lineIndex < lines.length) {
            String line = lines[lineIndex].trim();
            if (!line.isEmpty() && line.length() > 10) { // Basic filter for meaningful content
                
                RecommendationResponseDto recommendation = new RecommendationResponseDto();
                recommendation.setId((long) recommendations.size() + 1);
                recommendation.setZoneName(request.getZoneName());
                recommendation.setDate(currentDate);
                recommendation.setCropType(request.getCropType());
                recommendation.setCategory(request.getCategory());
                recommendation.setRecommendation(line);
                recommendation.setWaterSaved(calculateWaterSaved(line));
                recommendation.setRiskLevel(determineRiskLevel(line));
                recommendation.setTemperature(weatherData.getTempC());
                recommendation.setHumidity(weatherData.getHumidity());
                recommendation.setRainfall(weatherData.getRainfallMm());
                recommendation.setEtc(weatherData.getEtc() != null ? weatherData.getEtc() : 3.0);
                recommendation.setIsAIGenerated(true);
                recommendation.setWeatherDescription(weatherData.getWeatherDescription());
                recommendation.setAdditionalNotes("AI-generated based on current weather conditions");
                
                recommendations.add(recommendation);
                currentDate = currentDate.plusDays(1);
            }
            lineIndex++;
        }
        
        return recommendations;
    }
    
    
    private RecommendationResponseDto convertToResponseDto(IrrigationRecommendation rec) {
        RecommendationResponseDto dto = new RecommendationResponseDto();
        dto.setId(rec.getId());
        dto.setZoneName(rec.getLocationName());
        dto.setDate(rec.getDate());
        dto.setCropType(rec.getCropType());
        dto.setCategory("irrigation");
        dto.setRecommendation(rec.getRecommendation());
        dto.setWaterSaved(rec.getWaterSavedLiters() != null ? rec.getWaterSavedLiters().intValue() : 0);
        dto.setRiskLevel(rec.getRecommendation());
        dto.setTemperature(rec.getTempC());
        dto.setHumidity(rec.getHumidity());
        dto.setRainfall(rec.getRainfallMm());
        dto.setEtc(rec.getEvapotranspiration());
        dto.setIsAIGenerated(false);
        dto.setWeatherDescription("Historical data");
        dto.setAdditionalNotes(rec.getExplanation());
        
        return dto;
    }
    
    private Integer calculateWaterSaved(String recommendation) {
        // Simple heuristic based on recommendation text
        if (recommendation.toLowerCase().contains("high") || recommendation.toLowerCase().contains("increase")) {
            return 15 + (int)(Math.random() * 10);
        } else if (recommendation.toLowerCase().contains("moderate") || recommendation.toLowerCase().contains("maintain")) {
            return 8 + (int)(Math.random() * 7);
        } else {
            return 3 + (int)(Math.random() * 5);
        }
    }
    
    private String determineRiskLevel(String recommendation) {
        String lower = recommendation.toLowerCase();
        if (lower.contains("high") || lower.contains("urgent") || lower.contains("extreme")) {
            return "HIGH";
        } else if (lower.contains("moderate") || lower.contains("warning") || lower.contains("caution")) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
