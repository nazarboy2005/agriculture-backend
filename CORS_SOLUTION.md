# CORS Solution

## Problem
Frontend at `https://agriculture-frontend-two.vercel.app` was blocked by CORS policy when making requests to the backend.

## Solution
Simple Spring Boot CORS configuration with exact domain matching.

## Files
- `src/main/java/com/hackathon/agriculture_backend/config/CorsConfig.java` - CORS configuration
- `src/main/java/com/hackathon/agriculture_backend/config/SecurityConfig.java` - Security with CORS enabled
- `src/main/resources/application-prod.properties` - Production CORS settings

## Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://agriculture-frontend-two.vercel.app",
                            "https://agriculture-frontend.vercel.app",
                            "http://localhost:3000"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
```

## Deploy
```bash
railway deploy
```

## Test
Your frontend should now be able to make requests without CORS errors.
