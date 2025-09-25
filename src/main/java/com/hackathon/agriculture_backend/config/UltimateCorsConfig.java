package com.hackathon.agriculture_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@Slf4j
public class UltimateCorsConfig {

    @Bean
    public CorsFilter ultimateCorsFilter() {
        log.error("ULTIMATE CORS CONFIG - Creating ultimate CORS filter");
        
        CorsConfiguration config = new CorsConfiguration();
        
        // Set allowed origins
        config.setAllowedOrigins(Arrays.asList(
            "https://agriculture-frontend-two.vercel.app",
            "https://agriculture-frontend.vercel.app",
            "https://agriculture-frontend-btleirx65.vercel.app",
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));
        
        // Set allowed methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Set allowed headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Set max age
        config.setMaxAge(3600L);
        
        // Expose headers
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        log.error("ULTIMATE CORS CONFIG - CORS filter created with origins: {}", config.getAllowedOrigins());
        
        return new CorsFilter(source);
    }
}
