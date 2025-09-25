package com.hackathon.agriculture_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS is handled by SecurityConfig to avoid conflicts
    // This class is kept for other potential MVC configurations
}
