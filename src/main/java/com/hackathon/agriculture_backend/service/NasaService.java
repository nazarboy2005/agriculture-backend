package com.hackathon.agriculture_backend.service;

import com.hackathon.agriculture_backend.dto.NasaEtDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NasaService {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.nasa.api.url}")
    private String apiUrl;
    
    public Double getEvapotranspiration(Double latitude, Double longitude, LocalDate date) {
        log.info("Fetching evapotranspiration for coordinates: {}, {} on date: {}", latitude, longitude, date);
        
        try {
            String startDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            String url = String.format("%s?start=%s&end=%s&latitude=%s&longitude=%s&parameters=ET0&format=JSON",
                    apiUrl, startDate, endDate, latitude, longitude);
            
            NasaEtDto nasaData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(NasaEtDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (nasaData != null && 
                nasaData.getParameters() != null && 
                nasaData.getParameters().getEt0() != null && 
                nasaData.getParameters().getEt0().getData() != null && 
                !nasaData.getParameters().getEt0().getData().isEmpty()) {
                
                Double et0 = nasaData.getParameters().getEt0().getData().get(0);
                log.info("ET0 data fetched successfully: {} mm/day", et0);
                return et0;
            }
            
            log.warn("No ET0 data available for the specified date and location");
            return 0.0;
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching NASA ET0 data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch NASA ET0 data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching NASA ET0 data", e);
            throw new RuntimeException("Failed to fetch NASA ET0 data: " + e.getMessage());
        }
    }
    
    public Double getEvapotranspirationForDateRange(Double latitude, Double longitude, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching evapotranspiration for coordinates: {}, {} from {} to {}", 
                latitude, longitude, startDate, endDate);
        
        try {
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            String url = String.format("%s?start=%s&end=%s&latitude=%s&longitude=%s&parameters=ET0&format=JSON",
                    apiUrl, startDateStr, endDateStr, latitude, longitude);
            
            NasaEtDto nasaData = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(NasaEtDto.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (nasaData != null && 
                nasaData.getParameters() != null && 
                nasaData.getParameters().getEt0() != null && 
                nasaData.getParameters().getEt0().getData() != null && 
                !nasaData.getParameters().getEt0().getData().isEmpty()) {
                
                // Calculate average ET0 for the date range
                double sum = nasaData.getParameters().getEt0().getData().stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
                Double averageEt0 = sum / nasaData.getParameters().getEt0().getData().size();
                
                log.info("Average ET0 data fetched successfully: {} mm/day for {} days", 
                        averageEt0, nasaData.getParameters().getEt0().getData().size());
                return averageEt0;
            }
            
            log.warn("No ET0 data available for the specified date range and location");
            return 0.0;
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching NASA ET0 data for date range: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch NASA ET0 data for date range: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching NASA ET0 data for date range", e);
            throw new RuntimeException("Failed to fetch NASA ET0 data for date range: " + e.getMessage());
        }
    }
    
    public boolean isHighEvapotranspiration(Double et0, Double threshold) {
        return et0 != null && threshold != null && et0 > threshold;
    }
}


