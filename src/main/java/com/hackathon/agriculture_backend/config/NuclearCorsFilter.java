package com.hackathon.agriculture_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class NuclearCorsFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "https://agriculture-frontend-two.vercel.app",
        "https://agriculture-frontend.vercel.app",
        "https://agriculture-frontend-btleirx65.vercel.app"
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
        "Authorization",
        "Content-Type",
        "Accept",
        "Origin",
        "X-Requested-With",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers",
        "Cache-Control",
        "Pragma"
    );

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public OncePerRequestFilter nuclearCorsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                
                String origin = request.getHeader("Origin");
                String method = request.getMethod();
                
                // Log the request for debugging
                System.out.println("=== NUCLEAR CORS FILTER ===");
                System.out.println("Origin: " + origin);
                System.out.println("Method: " + method);
                System.out.println("Request URI: " + request.getRequestURI());
                
                // Check if origin is allowed
                boolean isOriginAllowed = origin != null && ALLOWED_ORIGINS.contains(origin);
                
                if (isOriginAllowed) {
                    // Set CORS headers aggressively
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));
                    response.setHeader("Access-Control-Allow-Headers", String.join(", ", ALLOWED_HEADERS));
                    response.setHeader("Access-Control-Max-Age", "86400"); // 24 hours
                    response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
                    
                    // Remove any existing CORS headers that Railway might have set
                    response.setHeader("Vary", "Origin");
                    
                    System.out.println("CORS headers set for origin: " + origin);
                } else {
                    System.out.println("Origin not allowed: " + origin);
                }
                
                // Handle preflight requests
                if ("OPTIONS".equals(method)) {
                    System.out.println("Handling OPTIONS preflight request");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                
                // Continue with the request
                filterChain.doFilter(request, response);
                
                // Log response headers
                System.out.println("Response headers after filter:");
                response.getHeaderNames().forEach(headerName -> {
                    System.out.println(headerName + ": " + response.getHeader(headerName));
                });
                System.out.println("=== END NUCLEAR CORS FILTER ===");
            }
        };
    }
}
