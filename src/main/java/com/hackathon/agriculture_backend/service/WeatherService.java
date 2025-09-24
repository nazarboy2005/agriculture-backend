package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.weather.api.key}")
    private String apiKey;
    
    @Value("${app.weather.api.url}")
    private String apiUrl;
    
    public WeatherDto getCurrentWeather(Double latitude, Double longitude) {
        log.info("Fetching current weather for coordinates: {}, {}", latitude, longitude);
        
        try {
            // Use One Call API 3.0 with optimized parameters
            String url = String.format("%s?lat=%s&lon=%s&exclude=minutely&units=metric&appid=%s",
                    apiUrl, latitude, longitude, apiKey);
            
            WeatherDto weatherData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (weatherData != null && weatherData.getCurrent() != null) {
                // Extract current weather data
                WeatherDto.CurrentWeather current = weatherData.getCurrent();
                weatherData.setTempC(current.getTemp());
                weatherData.setHumidity(current.getHumidity());
                
                // Calculate ETC for real weather data
                weatherData.setEtc(calculateETC(current.getTemp(), current.getHumidity()));
                
                // Extract current day rainfall from current data
                Double currentRainfall = 0.0;
                if (current.getRain() != null) {
                    currentRainfall = current.getRain().getOneHour();
                }
                weatherData.setRainfallMm(currentRainfall);
                
                // Extract forecast rainfall for tomorrow
                if (weatherData.getDaily() != null && !weatherData.getDaily().isEmpty()) {
                    WeatherDto.DailyWeather tomorrow = weatherData.getDaily().get(1); // Tomorrow's forecast
                    weatherData.setForecastRainfallMm(tomorrow.getRain() != null ? tomorrow.getRain() : 0.0);
                } else {
                    weatherData.setForecastRainfallMm(0.0);
                }
                
                // Check for heat alerts (temperature > 40°C)
                weatherData.setHeatAlert(weatherData.getTempC() != null && weatherData.getTempC() > 40.0);
                
                // Set weather description
                if (current.getWeather() != null && !current.getWeather().isEmpty()) {
                    weatherData.setWeatherDescription(current.getWeather().get(0).getDescription());
                }
                
                log.info("Weather data fetched successfully. Temp: {}°C, Humidity: {}%, Heat Alert: {}, Rainfall: {}mm", 
                        weatherData.getTempC(), weatherData.getHumidity(), weatherData.getHeatAlert(), weatherData.getRainfallMm());
            }
            
            return weatherData;
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching weather data", e);
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        }
    }
    
    public WeatherDto getWeatherForecast(Double latitude, Double longitude, int days) {
        log.info("Fetching weather forecast for coordinates: {}, {} for {} days", latitude, longitude, days);
        
        try {
            String url = String.format("%s?lat=%s&lon=%s&exclude=minutely,hourly&units=metric&appid=%s",
                    apiUrl, latitude, longitude, apiKey);
            
            WeatherDto weatherData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (weatherData != null && weatherData.getDaily() != null) {
                // Limit forecast to requested days
                if (weatherData.getDaily().size() > days) {
                    weatherData.setDaily(weatherData.getDaily().subList(0, days));
                }
                
                log.info("Weather forecast fetched successfully for {} days", weatherData.getDaily().size());
            }
            
            return weatherData;
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching weather forecast: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather forecast: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching weather forecast", e);
            throw new RuntimeException("Failed to fetch weather forecast: " + e.getMessage());
        }
    }
    
    public boolean isHeatAlert(Double temperature) {
        return temperature != null && temperature > 40.0;
    }
    
    public boolean isExtremeWeather(Double temperature, Double humidity) {
        return (temperature != null && temperature > 45.0) || 
               (humidity != null && humidity < 10.0);
    }
    
    /**
     * Get weather alerts for a location using One Call API 3.0
     */
    public List<WeatherDto.WeatherAlert> getWeatherAlerts(Double latitude, Double longitude) {
        log.info("Fetching weather alerts for coordinates: {}, {}", latitude, longitude);
        
        try {
            String url = String.format("%s?lat=%s&lon=%s&exclude=minutely,hourly,daily&units=metric&appid=%s",
                    apiUrl, latitude, longitude, apiKey);
            
            WeatherDto weatherData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (weatherData != null && weatherData.getAlerts() != null) {
                log.info("Found {} weather alerts", weatherData.getAlerts().size());
                return weatherData.getAlerts();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("Error fetching weather alerts", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get comprehensive weather data optimized for irrigation recommendations
     */
    public WeatherDto getIrrigationWeatherData(Double latitude, Double longitude) {
        log.info("Fetching comprehensive weather data for irrigation recommendations: {}, {}", latitude, longitude);
        
        try {
            // Get current weather with hourly forecast for better rainfall prediction
            String url = String.format("%s?lat=%s&lon=%s&exclude=minutely&units=metric&appid=%s",
                    apiUrl, latitude, longitude, apiKey);
            
            WeatherDto weatherData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (weatherData != null && weatherData.getCurrent() != null) {
                // Process current weather
                WeatherDto.CurrentWeather current = weatherData.getCurrent();
                weatherData.setTempC(current.getTemp());
                weatherData.setHumidity(current.getHumidity());
                weatherData.setRainfallMm(current.getRain() != null ? current.getRain().getOneHour() : 0.0);
                
                // Calculate ETC for real weather data
                weatherData.setEtc(calculateETC(current.getTemp(), current.getHumidity()));
                
                // Process daily forecast for better rainfall prediction
                if (weatherData.getDaily() != null && !weatherData.getDaily().isEmpty()) {
                    WeatherDto.DailyWeather tomorrow = weatherData.getDaily().get(1);
                    weatherData.setForecastRainfallMm(tomorrow.getRain() != null ? tomorrow.getRain() : 0.0);
                }
                
                // Check for heat alerts
                weatherData.setHeatAlert(weatherData.getTempC() != null && weatherData.getTempC() > 40.0);
                
                // Set weather description
                if (current.getWeather() != null && !current.getWeather().isEmpty()) {
                    weatherData.setWeatherDescription(current.getWeather().get(0).getDescription());
                }
                
                log.info("Comprehensive weather data fetched successfully for irrigation recommendations");
            }
            
            return weatherData;
            
        } catch (Exception e) {
            log.error("Error fetching comprehensive weather data", e);
            // Return mock data for invalid coordinates or API issues
            return createMockWeatherData(latitude, longitude);
        }
    }
    
    /**
     * Create mock weather data for fallback scenarios
     */
    private WeatherDto createMockWeatherData(Double latitude, Double longitude) {
        log.info("Creating mock weather data for coordinates: {}, {}", latitude, longitude);
        
        WeatherDto mockData = new WeatherDto();
        mockData.setTempC(25.0 + (Math.random() * 15)); // 25-40°C
        mockData.setHumidity(40.0 + (Math.random() * 40)); // 40-80%
        mockData.setRainfallMm(Math.random() * 5); // 0-5mm
        mockData.setForecastRainfallMm(Math.random() * 3); // 0-3mm
        mockData.setHeatAlert(mockData.getTempC() > 35.0);
        mockData.setWeatherDescription("Partly cloudy");
        // Calculate ETC based on temperature and humidity
        mockData.setEtc(calculateETC(mockData.getTempC(), mockData.getHumidity()));
        
        return mockData;
    }
    
    /**
     * Calculate Evapotranspiration (ETC) based on temperature and humidity
     */
    private Double calculateETC(Double temperature, Double humidity) {
        // Simplified ETC calculation using temperature and humidity
        // ETC = 0.0023 * (T + 17.8) * sqrt(Tmax - Tmin) * (1 - RH/100)
        // For simplicity, using a basic formula
        Double baseETC = 0.0023 * (temperature + 17.8) * Math.sqrt(temperature);
        Double humidityFactor = 1.0 - (humidity / 100.0);
        return baseETC * humidityFactor;
    }
}
