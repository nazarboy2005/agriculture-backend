package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.IrrigationPlanDto;
import com.hackathon.agriculture_backend.dto.WeatherDto;
import com.hackathon.agriculture_backend.controller.SmartIrrigationController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartIrrigationService {
    
    private final WeatherService weatherService;
    private final RecommendationService recommendationService;
    
    public IrrigationPlanDto generateIrrigationPlan(Double latitude, Double longitude, String cropType, 
                                                   Double area, String irrigationType, String soilType) {
        
        log.info("Generating irrigation plan for location: {}, {} with crop: {}", 
                latitude, longitude, cropType);
        
        try {
            // Get real weather data
            WeatherDto weatherData = weatherService.getIrrigationWeatherData(latitude, longitude);
            
            if (weatherData == null || weatherData.getCurrent() == null) {
                throw new RuntimeException("Unable to fetch weather data for the location");
            }
            
            // Extract current weather conditions with null checks
            Double temperature = weatherData.getCurrent().getTemp() != null ? 
                    weatherData.getCurrent().getTemp() : 25.0;
            Double humidity = weatherData.getCurrent().getHumidity() != null ? 
                    weatherData.getCurrent().getHumidity() : 50.0;
            Double rainfall = weatherData.getCurrent().getRain() != null ? 
                    weatherData.getCurrent().getRain().getOneHour() : 0.0;
            Double windSpeed = weatherData.getCurrent().getWindSpeed() != null ? 
                    weatherData.getCurrent().getWindSpeed() : 5.0;
            Double uvIndex = weatherData.getCurrent().getUvi() != null ? 
                    weatherData.getCurrent().getUvi() : 5.0;
            
            // Calculate evapotranspiration using real weather data
            Double et0 = calculateEvapotranspiration(temperature, humidity, windSpeed, uvIndex);
            
            // Generate 7-day irrigation plan
            List<IrrigationPlanDto.DayPlan> dailyPlans = new ArrayList<>();
            
            for (int i = 0; i < 7; i++) {
                LocalDate date = LocalDate.now().plusDays(i);
                
                // Get forecast data for each day
                WeatherDto.DailyWeather dayForecast = null;
                if (weatherData.getDaily() != null && i < weatherData.getDaily().size()) {
                    dayForecast = weatherData.getDaily().get(i);
                }
                
                // Calculate irrigation needs for each day
                IrrigationPlanDto.DayPlan dayPlan = calculateDayPlan(
                        date, cropType, area, irrigationType, soilType,
                        dayForecast, et0, temperature, humidity, rainfall, windSpeed
                );
                
                dailyPlans.add(dayPlan);
            }
            
            // Create comprehensive irrigation plan
            IrrigationPlanDto plan = new IrrigationPlanDto();
            plan.setLocationName(getLocationName(latitude, longitude));
            plan.setCropType(cropType);
            plan.setArea(area);
            plan.setIrrigationType(irrigationType);
            plan.setSoilType(soilType);
            plan.setDailyPlans(dailyPlans);
            plan.setGeneratedAt(LocalDate.now());
            
            // Calculate total water savings
            Double totalWaterSaved = dailyPlans.stream()
                    .mapToDouble(IrrigationPlanDto.DayPlan::getWaterSavedLiters)
                    .sum();
            plan.setTotalWaterSaved(totalWaterSaved);
            
            log.info("Irrigation plan generated successfully with {} days of recommendations", 
                    dailyPlans.size());
            
            return plan;
            
        } catch (Exception e) {
            log.error("Error generating irrigation plan: {}", e.getMessage());
            throw new RuntimeException("Failed to generate irrigation plan: " + e.getMessage());
        }
    }
    
    public List<SmartIrrigationController.HeatAlertDto> getHeatAlerts(Double latitude, Double longitude) {
        log.info("Fetching heat alerts for location: {}, {}", latitude, longitude);
        
        try {
            WeatherDto weatherData = weatherService.getIrrigationWeatherData(latitude, longitude);
            List<SmartIrrigationController.HeatAlertDto> alerts = new ArrayList<>();
            
            if (weatherData != null && weatherData.getDaily() != null) {
                for (int i = 0; i < Math.min(7, weatherData.getDaily().size()); i++) {
                    WeatherDto.DailyWeather day = weatherData.getDaily().get(i);
                    
                    if (day.getTemp() != null && day.getTemp().getMax() != null && day.getTemp().getMax() > 35.0) {
                        SmartIrrigationController.HeatAlertDto alert = new SmartIrrigationController.HeatAlertDto();
                        alert.setDate(LocalDate.now().plusDays(i).toString());
                        alert.setTime("12:00");
                        alert.setTemperature(day.getTemp().getMax());
                        alert.setHeatIndex(calculateHeatIndex(day.getTemp().getMax(), 
                                day.getHumidity() != null ? day.getHumidity() : 50.0));
                        alert.setRiskLevel(determineHeatRisk(day.getTemp().getMax()));
                        alert.setRecommendations(generateHeatRecommendations(day.getTemp().getMax()));
                        
                        alerts.add(alert);
                    }
                }
            }
            
            return alerts;
            
        } catch (Exception e) {
            log.error("Error fetching heat alerts: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public SmartIrrigationController.WeatherDataDto getWeatherData(Double latitude, Double longitude) {
        log.info("Fetching weather data for location: {}, {}", latitude, longitude);
        
        try {
            WeatherDto weatherData = weatherService.getIrrigationWeatherData(latitude, longitude);
            
            if (weatherData == null || weatherData.getCurrent() == null) {
                throw new RuntimeException("Unable to fetch weather data");
            }
            
            SmartIrrigationController.WeatherDataDto weatherDto = new SmartIrrigationController.WeatherDataDto();
            weatherDto.setTemperature(weatherData.getCurrent().getTemp() != null ? 
                    weatherData.getCurrent().getTemp() : 25.0);
            weatherDto.setHumidity(weatherData.getCurrent().getHumidity() != null ? 
                    weatherData.getCurrent().getHumidity() : 50.0);
            weatherDto.setRainfall(weatherData.getCurrent().getRain() != null ? 
                    weatherData.getCurrent().getRain().getOneHour() : 0.0);
            weatherDto.setWindSpeed(weatherData.getCurrent().getWindSpeed() != null ? 
                    weatherData.getCurrent().getWindSpeed() : 5.0);
            weatherDto.setUvIndex(weatherData.getCurrent().getUvi() != null ? 
                    weatherData.getCurrent().getUvi() : 5.0);
            weatherDto.setHeatRisk(determineHeatRisk(weatherData.getCurrent().getTemp() != null ? 
                    weatherData.getCurrent().getTemp() : 25.0));
            
            return weatherDto;
            
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        }
    }
    
    private IrrigationPlanDto.DayPlan calculateDayPlan(LocalDate date, String cropType, Double area,
                                                     String irrigationType, String soilType,
                                                     WeatherDto.DailyWeather forecast, Double et0,
                                                     Double currentTemp, Double currentHumidity, Double rainfall, Double windSpeed) {
        
        IrrigationPlanDto.DayPlan dayPlan = new IrrigationPlanDto.DayPlan();
        dayPlan.setDate(date);
        
        // Use forecast data if available, otherwise use current data
        Double temperature = forecast != null && forecast.getTemp() != null ? 
                forecast.getTemp().getMax() : currentTemp;
        Double humidity = forecast != null && forecast.getHumidity() != null ? 
                forecast.getHumidity() : currentHumidity;
        Double dayRainfall = forecast != null && forecast.getRain() != null ? 
                forecast.getRain() : rainfall;
        
        // Calculate crop water requirements based on crop type and area
        Double cropWaterRequirement = calculateCropWaterRequirement(cropType, area, temperature, humidity);
        
        // Calculate irrigation efficiency based on irrigation type and soil type
        Double irrigationEfficiency = calculateIrrigationEfficiency(irrigationType, soilType);
        
        // Calculate actual irrigation needed
        Double irrigationNeeded = Math.max(0, cropWaterRequirement - dayRainfall);
        Double adjustedIrrigation = irrigationNeeded / irrigationEfficiency;
        
        // Calculate irrigation duration (assuming 2 L/min flow rate for drip irrigation)
        Double flowRate = getFlowRate(irrigationType);
        Double durationMinutes = (adjustedIrrigation / flowRate) * 60;
        
        // Calculate optimal irrigation time (early morning)
        LocalTime optimalTime = calculateOptimalIrrigationTime(temperature, humidity, windSpeed);
        
        // Calculate water savings
        Double waterSaved = calculateWaterSavings(irrigationNeeded, adjustedIrrigation);
        
        // Set plan details
        dayPlan.setEtc(et0);
        dayPlan.setLiters(adjustedIrrigation);
        dayPlan.setMinutes(durationMinutes.intValue());
        dayPlan.setTime(optimalTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        dayPlan.setHeatRisk(determineHeatRisk(temperature));
        dayPlan.setWaterSavedLiters(waterSaved);
        dayPlan.setNotes(generateDayNotes(temperature, humidity, dayRainfall, irrigationNeeded));
        
        return dayPlan;
    }
    
    private Double calculateEvapotranspiration(Double temperature, Double humidity, Double windSpeed, Double uvIndex) {
        // Simplified ET0 calculation using temperature, humidity, and wind speed
        Double et0 = 0.0023 * (temperature + 17.8) * Math.sqrt(Math.max(0, temperature - 0)) * 
                (1 - humidity / 100) * (1 + windSpeed / 10);
        
        // Adjust for UV index
        et0 *= (1 + uvIndex / 20);
        
        return Math.max(0.1, et0); // Minimum ET0 of 0.1
    }
    
    private Double calculateCropWaterRequirement(String cropType, Double area, Double temperature, Double humidity) {
        // Base water requirements per crop type (L/m²/day)
        Double baseRequirement = getBaseWaterRequirement(cropType);
        
        // Adjust for temperature (higher temp = more water)
        Double tempAdjustment = 1 + (temperature - 25) * 0.02;
        
        // Adjust for humidity (lower humidity = more water)
        Double humidityAdjustment = 1 + (50 - humidity) * 0.01;
        
        return baseRequirement * area * tempAdjustment * humidityAdjustment;
    }
    
    private Double getBaseWaterRequirement(String cropType) {
        switch (cropType.toLowerCase()) {
            case "tomato": return 6.0;
            case "cucumber": return 5.5;
            case "lettuce": return 3.0;
            case "pepper": return 5.0;
            case "wheat": return 4.0;
            case "corn": return 7.0;
            case "rice": return 10.0;
            default: return 5.0;
        }
    }
    
    private Double calculateIrrigationEfficiency(String irrigationType, String soilType) {
        Double irrigationEfficiency = 0.8; // Base efficiency
        
        // Adjust for irrigation type
        switch (irrigationType.toLowerCase()) {
            case "drip": irrigationEfficiency = 0.95; break;
            case "sprinkler": irrigationEfficiency = 0.85; break;
            case "flood": irrigationEfficiency = 0.60; break;
        }
        
        // Adjust for soil type
        switch (soilType.toLowerCase()) {
            case "sandy": irrigationEfficiency *= 0.9; break;
            case "clay": irrigationEfficiency *= 1.1; break;
            case "loam": irrigationEfficiency *= 1.0; break;
            case "silt": irrigationEfficiency *= 1.05; break;
        }
        
        return Math.min(0.95, irrigationEfficiency);
    }
    
    private Double getFlowRate(String irrigationType) {
        switch (irrigationType.toLowerCase()) {
            case "drip": return 2.0; // L/min
            case "sprinkler": return 10.0; // L/min
            case "flood": return 50.0; // L/min
            default: return 2.0;
        }
    }
    
    private LocalTime calculateOptimalIrrigationTime(Double temperature, Double humidity, Double windSpeed) {
        // Optimal time is early morning (5-7 AM) when temperature is lowest and humidity is highest
        int hour = 6;
        int minute = 0;
        
        // Adjust based on conditions
        if (temperature > 35) {
            hour = 5; // Earlier for hot days
        } else if (temperature < 20) {
            hour = 7; // Later for cool days
        }
        
        return LocalTime.of(hour, minute);
    }
    
    private Double calculateWaterSavings(Double irrigationNeeded, Double adjustedIrrigation) {
        return Math.max(0, adjustedIrrigation - irrigationNeeded);
    }
    
    private String determineHeatRisk(Double temperature) {
        if (temperature > 40) return "EXTREME";
        if (temperature > 35) return "HIGH";
        if (temperature > 30) return "MODERATE";
        return "LOW";
    }
    
    private Double calculateHeatIndex(Double temperature, Double humidity) {
        // Simplified heat index calculation
        return temperature + (humidity - 50) * 0.1;
    }
    
    private String generateHeatRecommendations(Double temperature) {
        if (temperature > 40) {
            return "Emergency cooling required. Increase irrigation frequency and provide shade.";
        } else if (temperature > 35) {
            return "High heat risk. Increase irrigation and monitor soil moisture closely.";
        } else if (temperature > 30) {
            return "Moderate heat. Normal irrigation schedule with extra monitoring.";
        } else {
            return "Normal conditions. Follow standard irrigation schedule.";
        }
    }
    
    private String generateDayNotes(Double temperature, Double humidity, Double rainfall, Double irrigationNeeded) {
        StringBuilder notes = new StringBuilder();
        
        if (temperature > 35) {
            notes.append("High heat - increase irrigation. ");
        }
        if (humidity < 30) {
            notes.append("Low humidity - more water needed. ");
        }
        if (rainfall > 5) {
            notes.append("Rainfall reduces irrigation needs. ");
        }
        if (irrigationNeeded < 1) {
            notes.append("Minimal irrigation required. ");
        }
        
        return notes.toString().trim();
    }
    
    private String getLocationName(Double latitude, Double longitude) {
        // Simple location naming based on coordinates
        if (latitude > 25.0 && latitude < 26.0 && longitude > 51.0 && longitude < 52.0) {
            return "Doha, Qatar";
        } else if (latitude > 24.0 && latitude < 25.0 && longitude > 51.0 && longitude < 52.0) {
            return "Al-Wakrah, Qatar";
        } else {
            return String.format("Location (%.4f, %.4f)", latitude, longitude);
        }
    }
}
