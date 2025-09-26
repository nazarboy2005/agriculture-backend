# CORS Solution

## Problem
Frontend at `https://agriculture-frontend-two.vercel.app` was blocked by CORS policy. The issue was using `Access-Control-Allow-Origin: *` with `allowCredentials(true)`, which browsers don't allow.

## Solution
Spring Boot CORS configuration with **exact domain matching** instead of wildcard.

## Key Files
- `src/main/java/com/hackathon/agriculture_backend/config/CorsConfig.java` - CORS configuration
- `src/main/java/com/hackathon/agriculture_backend/config/SecurityConfig.java` - Security with CORS enabled
- `src/main/resources/application-prod.properties` - Production settings

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
                        .allowCredentials(true)  // Requires exact domain, not "*"
                        .maxAge(3600);
            }
        };
    }
}
```

## Why This Works
- **Exact domain matching** instead of wildcard `*`
- **allowCredentials(true)** works with specific domains
- **No wildcard + credentials conflict**

## Deploy
```bash
railway deploy
```

## Test
Your frontend should now be able to make requests without CORS errors.
